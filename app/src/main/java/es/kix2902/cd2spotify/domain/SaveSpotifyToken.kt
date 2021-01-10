package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope

class SaveSpotifyToken(
    scope: CoroutineScope,
    private val preferencesRepository: PreferencesRepository
) : UseCase<UseCase.None, String>(scope) {

    override suspend fun run(params: String): Result {
        preferencesRepository.saveSpotifyToken(params)
        return Result.Success(None())
    }
}