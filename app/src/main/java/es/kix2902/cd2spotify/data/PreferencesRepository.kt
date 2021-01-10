package es.kix2902.cd2spotify.data

import android.content.Context
import androidx.preference.PreferenceManager
import es.kix2902.cd2spotify.helpers.SingletonHolder

class PreferencesRepository private constructor(context: Context) {

    companion object : SingletonHolder<PreferencesRepository, Context>(::PreferencesRepository) {
        private const val KEY_SPOTIFY_TOKEN = "spotify-token"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    suspend fun hasSpotifyToken(): Boolean {
        return preferences.contains(KEY_SPOTIFY_TOKEN)
    }

    suspend fun saveSpotifyToken(token: String) {
        preferences.edit().putString(KEY_SPOTIFY_TOKEN, token).apply()
    }

    suspend fun loadSpotifyToken(): String {
        return preferences.getString(KEY_SPOTIFY_TOKEN, "") ?: ""
    }
}