package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope

class IsSpotifyInitialized(
    scope: CoroutineScope,
    private val preferencesRepository: PreferencesRepository
) : UseCase<Boolean, UseCase.None>(scope) {

    override suspend fun run(params: None): Result {
        val authState = preferencesRepository.loadSpotifyAuth()
        return Result.Success(authState != null)
    }
}