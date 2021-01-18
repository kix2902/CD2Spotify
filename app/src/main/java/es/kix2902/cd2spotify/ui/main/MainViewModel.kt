package es.kix2902.cd2spotify.ui.main

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.kix2902.cd2spotify.data.PreferencesRepository
import es.kix2902.cd2spotify.domain.IsSpotifyInitialized
import es.kix2902.cd2spotify.domain.SaveSpotifyAuth
import es.kix2902.cd2spotify.domain.UseCase
import net.openid.appauth.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SPOTIFY_CLIENT_ID = "5bf1991a3f384cfa8c330b7174cb703a"
        private const val SPOTIFY_REDIRECT_URL = "cd2spotify://callback"
    }

    private val _spotifyInit = MutableLiveData(false)
    val spotifyInit: LiveData<Boolean> = _spotifyInit

    private val preferencesRepository =
        PreferencesRepository.getInstance(application.applicationContext)

    private val isSpotifyInitialized = IsSpotifyInitialized(viewModelScope, preferencesRepository)
    private val saveSpotifyToken = SaveSpotifyAuth(viewModelScope, preferencesRepository)

    fun checkSpotify() {
        isSpotifyInitialized.invoke(UseCase.None()) {
            val result = it as UseCase.Result.Success<*>
            _spotifyInit.value = result.data as Boolean
        }
    }

    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://accounts.spotify.com/authorize"),
        Uri.parse("https://accounts.spotify.com/api/token")
    )
    private val authState = AuthState(serviceConfig)
    val authRequest = AuthorizationRequest.Builder(
        serviceConfig,
        SPOTIFY_CLIENT_ID,
        ResponseTypeValues.CODE,
        Uri.parse(SPOTIFY_REDIRECT_URL)
    ).setScopes(
        "playlist-modify-public",
        "user-follow-modify",
        "user-follow-read",
        "user-library-modify",
        "user-read-private",
        "user-library-read"
    ).build()

    fun requestAccessToken(data: Intent, authService: AuthorizationService) {
        val resp = AuthorizationResponse.fromIntent(data)
        val ex = AuthorizationException.fromIntent(data)
        authState.update(resp, ex)

        if (resp != null) {
            authService.performTokenRequest(resp.createTokenExchangeRequest()) { newResp, newEx ->
                authState.update(newResp, newEx)
                if (newResp != null) {
                    saveSpotifyToken.invoke(authState) {
                        _spotifyInit.value = true
                    }
                } else {
                    _spotifyInit.value = false
                }
            }

        } else {
            _spotifyInit.value = false
        }
    }
}