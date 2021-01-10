package es.kix2902.cd2spotify.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.kix2902.cd2spotify.data.PreferencesRepository
import es.kix2902.cd2spotify.domain.IsSpotifyInitialized
import es.kix2902.cd2spotify.domain.SaveSpotifyToken
import es.kix2902.cd2spotify.domain.UseCase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _spotifyInit = MutableLiveData(false)
    val spotifyInit: LiveData<Boolean> = _spotifyInit

    private val preferencesRepository =
        PreferencesRepository.getInstance(application.applicationContext)

    private val isSpotifyInitialized = IsSpotifyInitialized(viewModelScope, preferencesRepository)
    private val saveSpotifyToken = SaveSpotifyToken(viewModelScope, preferencesRepository)

    fun checkSpotify() {
        isSpotifyInitialized.invoke(UseCase.None()) {
            val result = it as UseCase.Result.Success<*>
            _spotifyInit.value = result.data as Boolean
        }
    }

    fun saveSpotifyToken(token: String) {
        saveSpotifyToken.invoke(token) {
            _spotifyInit.value = true
        }
    }
}