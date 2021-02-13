package es.kix2902.cd2spotify.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.models.Musicbrainz
import es.kix2902.cd2spotify.domain.GetAllReleases
import es.kix2902.cd2spotify.domain.UseCase

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dbRepository = DatabaseRepository.getInstance(application.applicationContext)

    private val getAllReleases = GetAllReleases(viewModelScope, dbRepository)

    private val _releases = MutableLiveData<List<Musicbrainz.ReleaseWithArtists>>(listOf())
    val releases: LiveData<List<Musicbrainz.ReleaseWithArtists>> = _releases

    init {
        getAllReleases.invoke(UseCase.None()) { result ->
            if (result is UseCase.Result.Success<*>) {
                _releases.value = result.data as List<Musicbrainz.ReleaseWithArtists>
            }
        }
    }
}