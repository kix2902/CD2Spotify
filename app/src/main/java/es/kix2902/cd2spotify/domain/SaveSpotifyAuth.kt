package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import net.openid.appauth.AuthState

class SaveSpotifyAuth(
    scope: CoroutineScope,
    private val preferencesRepository: PreferencesRepository
) : UseCase<UseCase.None, AuthState>(scope) {

    override suspend fun run(params: AuthState): Result {
        preferencesRepository.saveSpotifyAuth(params)
        return Result.Success(None())
    }
}