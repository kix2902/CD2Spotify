package es.kix2902.cd2spotify.data

import es.kix2902.cd2spotify.BuildConfig
import es.kix2902.cd2spotify.data.models.Release
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await

object NetworkRepository {

    private const val MUSICBRAINZ_BASE_URL =
        "https://musicbrainz.org/ws/2/release/?fmt=json&query=barcode:"

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

    suspend fun searchReleaseByBarcode(barcode: String): Release? {
        val request = Request.Builder()
            .url(MUSICBRAINZ_BASE_URL + barcode)
            .build()

        val response = client.newCall(request).await()

        if (response.isSuccessful) {
            return withContext(Dispatchers.IO) {
                val json = JSONObject(response.body!!.string())
                val releases = json.optJSONArray("releases")
                if ((releases == null) || (releases.length() == 0)) {
                    return@withContext null
                }

                val release0 = releases.getJSONObject(0)
                val title = release0.optString("title")
                val artists = release0.optJSONArray("artist-credit")
                val artist = artists?.optJSONObject(0)?.optString("name") ?: ""

                return@withContext Release(barcode, title, artist)
            }

        } else {
            return null
        }
    }
}