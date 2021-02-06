package es.kix2902.cd2spotify.data

import android.content.Context
import es.kix2902.cd2spotify.BuildConfig
import es.kix2902.cd2spotify.data.models.Release
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
                    .header("User-Agent", "CD2Spotify/0.1 ( ismael.kix2902+cd2spotify@gmail.com )")
                    .build()
            )
        }
        .build()

    private val preferencesRepository = PreferencesRepository.getInstance(context)
    private val authService: AuthorizationService by lazy { AuthorizationService(context) }
    private val saveSpotifyAuth = SaveSpotifyAuth(CoroutineScope(Job()), preferencesRepository)

    private val spotifyClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(
            SpotifyAuthInterceptor(preferencesRepository, authService, saveSpotifyAuth)
        )
        .build()

    suspend fun findReleaseByBarcode(barcode: String): Release? {
        val request = Request.Builder()
            .url(MUSICBRAINZ_BASE_URL + barcode)
            .build()

        val response = client.newCall(request).await()

        if (response.isSuccessful) {
            return withContext(Dispatchers.IO) {
                val json = JSONObject(response.body!!.string())
                val releases = json.optJSONArray("releases")
                if ((releases == null) || (releases.length() == 0)) {
                    return@withContext Release(barcode)
                }

                val album = releases.getJSONObject(0)
                val title = album.optString("title")
                val artists = album.optJSONArray("artist-credit")
                val artist = artists?.optJSONObject(0)?.optString("name") ?: ""

                return@withContext Release(barcode, title, artist)
            }

        } else {
            return null
        }
    }

    suspend fun findSpotifyByRelease(release: Release): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=album:${release.title}%20artist:${release.author}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            return withContext(Dispatchers.IO) {
                val json = JSONObject(response.body!!.string())
                return@withContext parseSpotifyResponse(json)
            }

        } else {
            return null
        }
    }

    suspend fun findSpotifyByQuery(release: Release): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=${release.title}%20${release.author}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            return withContext(Dispatchers.IO) {
                val json = JSONObject(response.body!!.string())
                return@withContext parseSpotifyResponse(json)
            }

        } else {
            return null
        }
    }

    suspend fun findSpotifyByBarcode(release: Release): Spotify.Album? {
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?type=album&limit=1&q=upn:${release.barcode}")
            .build()

        val response = spotifyClient.newCall(request).await()

        if (response.isSuccessful) {
            return withContext(Dispatchers.IO) {
                val json = JSONObject(response.body!!.string())
                return@withContext parseSpotifyResponse(json)
            }

        } else {
            return null
        }
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