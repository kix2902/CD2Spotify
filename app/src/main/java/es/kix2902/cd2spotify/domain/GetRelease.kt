package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Release
import kotlinx.coroutines.CoroutineScope

class GetRelease(
    scope: CoroutineScope,
    private val dbRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository
) : UseCase<Release, String>(scope) {

    override suspend fun run(params: String): Result {
        var release = dbRepository.searchBarcode(params)

        if (release != null) {
            return Result.Success(release)

        } else {
            release = networkRepository.searchReleaseByBarcode(params)

            if (release != null) {
                dbRepository.insertRelease(release)

                return Result.Success(release)
            } else {
                return Result.Error
            }
        }
    }
}