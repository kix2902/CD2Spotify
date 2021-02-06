package es.kix2902.cd2spotify.ui.spotify

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Release
import es.kix2902.cd2spotify.domain.FindRelease
import es.kix2902.cd2spotify.domain.SearchAlbum
import es.kix2902.cd2spotify.domain.UseCase

class SpotifyViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository = DatabaseRepository.getInstance(application.applicationContext)
    private val networkRepository = NetworkRepository.getInstance(application.applicationContext)

    private val getRelease = FindRelease(viewModelScope, dbRepository, networkRepository)
    private val searchAlbum = SearchAlbum(viewModelScope, networkRepository)

    fun findBarcode(barcode: String) {
        getRelease.invoke(barcode) {
            when (it) {
                is UseCase.Result.Success<*> -> {
                    val release = it.data as Release
                    searchSpotify(release)

                    Toast.makeText(
                        getApplication(),
                        "${release.author} - ${release.title}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is UseCase.Result.Error -> {
                    Toast.makeText(getApplication(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun searchSpotify(release: Release) {
        searchAlbum.invoke(release) {
        }
    }
}