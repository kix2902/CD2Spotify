package es.kix2902.cd2spotify.data

import android.content.Context
import androidx.preference.PreferenceManager
import es.kix2902.cd2spotify.helpers.SingletonHolder
import net.openid.appauth.AuthState

class PreferencesRepository private constructor(context: Context) {

    companion object : SingletonHolder<PreferencesRepository, Context>(::PreferencesRepository) {
        private const val KEY_SPOTIFY_AUTH = "spotify-auth"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    suspend fun saveSpotifyAuth(authState: AuthState) {
        preferences.edit().putString(KEY_SPOTIFY_AUTH, authState.jsonSerializeString()).apply()
    }

    fun loadSpotifyAuth(): AuthState? {
        val stateJson = preferences.getString(KEY_SPOTIFY_AUTH, null)
        if (stateJson != null) {
            return AuthState.jsonDeserialize(stateJson)
        } else {
            return null
        }
    }

    fun clearSpotifyAuth() {
        preferences.edit().remove(KEY_SPOTIFY_AUTH).apply()
    }
}