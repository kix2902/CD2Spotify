package es.kix2902.cd2spotify.ui.spotify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Musicbrainz
import es.kix2902.cd2spotify.domain.FindRelease
import es.kix2902.cd2spotify.domain.FindSpotifyAlbum
import es.kix2902.cd2spotify.domain.UseCase
import es.kix2902.cd2spotify.helpers.SingleLiveEvent
import es.kix2902.cd2spotify.ui.error.ErrorActivity

class SpotifyViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository = DatabaseRepository.getInstance(application.applicationContext)
    private val networkRepository = NetworkRepository.getInstance(application.applicationContext)

    private val findRelease = FindRelease(viewModelScope, dbRepository, networkRepository)
    private val findSpotifyAlbum = FindSpotifyAlbum(viewModelScope, networkRepository)

    private val _errorLiveData = SingleLiveEvent<Int>()
    val error: LiveData<Int> = _errorLiveData

    fun findBarcode(barcode: String) {
        findRelease.invoke(barcode) {
            when (it) {
                is UseCase.Result.Success<*> -> {
                    val release = it.data as Musicbrainz.ReleaseWithArtists
                    findSpotify(release)
                }
                is UseCase.Result.Error -> {
                    _errorLiveData.value = ErrorActivity.ERROR_RELEASE_NOT_FOUND
                }
            }
        }
    }

    private fun findSpotify(release: Musicbrainz.ReleaseWithArtists) {
        findSpotifyAlbum.invoke(release) {
            when (it) {
                is UseCase.Result.Success<*> -> {

                }
                UseCase.Result.Error -> {
                    _errorLiveData.value = ErrorActivity.ERROR_SPOTIFY_NOT_FOUND
                }
            }
        }
    }
}