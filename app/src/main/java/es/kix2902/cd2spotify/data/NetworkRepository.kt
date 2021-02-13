package es.kix2902.cd2spotify.data

import android.content.Context
import android.webkit.URLUtil
import es.kix2902.cd2spotify.BuildConfig
import es.kix2902.cd2spotify.data.models.Musicbrainz
import es.kix2902.cd2spotify.data.models.Spotify
import es.kix2902.cd2spotify.domain.SaveSpotifyAuth
import es.kix2902.cd2spotify.helpers.SingletonHolder
import es.kix2902.cd2spotify.helpers.SpotifyAuthInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await

class NetworkRepository private constructor(context: Context) {

    companion object : SingletonHolder<NetworkRepository, Context>(::NetworkRepository) {
        private const val MUSICBRAINZ_BASE_URL =
            "https://musicbrainz.org/ws/2/release/?fmt=json&query=barcode:"
    }

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addNetworkInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "CD2Spotify/${BuildConfig.VERSION_NAME} ( ismael.kix2902+cd2spotify@gmail.com )"
                    )
                    .build()
            )
        }
        .build()

    private val preferencesRepository = PreferencesRepository.getInstance(context)
    private val databaseRepository = DatabaseRepository.getInstance(context)

    private val authService: AuthorizationService by lazy { AuthorizationService(context) }
    private val saveSpotifyAuth = SaveSpotifyAuth(CoroutineScope(Job()), preferencesRepository)

    private val spotifyClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(
            SpotifyAuthInterceptor(preferencesRepository, authService, saveSpotifyAuth)
        )
        .build()

    suspend fun findReleaseByBarcode(barcode: String): Boolean {
        val request = Request.Builder()
            .url(MUSICBRAINZ_BASE_URL + barcode)
            .build()

        val response = client.newCall(request).await()

        if (response.isSuccessful) {
            val json = withContext(Dispatchers.IO) { JSONObject(response.body!!.string()) }
            val releases = json.optJSONArray("releases")
            if ((releases != null) && (releases.length() > 0)) {
                val releaseData = releases.optJSONObject(0)
                val title = releaseData.optString("title")
                if (title.isNotEmpty()) {
                    val release = Musicbrainz.Release(barcode, title)
                    databaseRepository.insertRelease(release)
                }

                val artistsData = releaseData.optJSONArray("artist-credit")
                if (artistsData != null) {
                    for (i in 0 until artistsData.length()) {
                        val artistData = artistsData.optJSONObject(i).optJSONObject("artist")
                        val id = artistData?.optString("id")
                        val name = artistData?.optString("name")

                        if ((id != null) && (name != null)) {
                            val artist = Musicbrainz.Artist(id, name)
                            databaseRepository.insertArtist(artist)

                            val releaseArtist = Musicbrainz.ReleaseArtist(barcode, id)
                            databaseRepository.insertReleaseArtist(releaseArtist)
                        }
                    }
                }
            }
            return true

        } else {
            return false
        }
    }

    suspend fun findSpotifyByRelease(release: Musicbrainz.ReleaseWithArtists): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=album:${release.release.title}%20artist:${release.artists[0].name}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            val json = withContext(Dispatchers.IO) { JSONObject(response.body!!.string()) }
            return parseSpotifyResponse(json)

        } else {
            return null
        }
    }

    suspend fun findSpotifyByQuery(release: Musicbrainz.ReleaseWithArtists): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=${release.release.title}%20${release.artists[0].name}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            val json = withContext(Dispatchers.IO) { JSONObject(response.body!!.string()) }
            return parseSpotifyResponse(json)

        } else {
            return null
        }
    }

    suspend fun findSpotifyByBarcode(release: Musicbrainz.ReleaseWithArtists): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=upn:${release.release.barcode}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            val json = withContext(Dispatchers.IO) { JSONObject(response.body!!.string()) }
            return parseSpotifyResponse(json)

        } else {
            return null
        }
    }

    suspend fun findSpotifyTracks(albumId: String): List<Spotify.Track> {
        var url: String? = "https://api.spotify.com/v1/albums/$albumId/tracks"

        val tracks = arrayListOf<Spotify.Track>()
        do {
            val request = Request.Builder()
                .url(url!!)
                .build()

            val response = spotifyClient.newCall(request).await()

            if (response.isSuccessful) {
                val json = withContext(Dispatchers.IO) { JSONObject(response.body!!.string()) }
                url = json.optString("next")

                val items = json.optJSONArray("items")
                if (items != null) {
                    for (i in 0 until items.length()) {
                        val trackData = items.optJSONObject(i)
                        val id = trackData.optString("id")
                        val title = trackData.optString("name")
                        val discNumber = trackData.optInt("disc_number")
                        val trackNumber = trackData.optInt("track_number")

                        tracks.add(Spotify.Track(id, title, discNumber, trackNumber))
                    }
                }

            } else {
                url = null
            }
        } while (URLUtil.isValidUrl(url))

        return tracks
    }

    private suspend fun parseSpotifyResponse(json: JSONObject): Spotify.Album? {
        val albums = json.optJSONObject("albums")
        if (albums == null) {
            return null
        }

        val items = albums.optJSONArray("items")
        if ((items == null) || (items.length() == 0)) {
            return null
        }

        val item = items.optJSONObject(0)
        val id = item.optString("id")
        val title = item.optString("name")
        var artist: String? = null
        var image: String? = null

        val artists = item.optJSONArray("artists")
        if ((artists != null) && (artists.length() > 0)) {
            val artistData = artists.optJSONObject(0)
            artist = artistData.optString("name")
        }

        val images = item.optJSONArray("images")
        if ((images != null) && (images.length() > 0)) {
            val imagesData = images.optJSONObject(0)
            image = imagesData.optString("url")
        }

        return Spotify.Album(id, artist, title, image)
    }
}