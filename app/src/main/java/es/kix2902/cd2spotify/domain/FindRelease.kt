package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Release
import es.kix2902.cd2spotify.data.models.hasInfo
import kotlinx.coroutines.CoroutineScope

class FindRelease(
    scope: CoroutineScope,
    private val dbRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository
) : UseCase<Release, String>(scope) {

    override suspend fun run(params: String): Result {
        var release = dbRepository.searchBarcode(params)

        if (release != null) {
            return Result.Success(release)

        } else {
            release = networkRepository.findReleaseByBarcode(params)

            if (release != null) {
                if (release.hasInfo()) {
                    dbRepository.insertRelease(release)
                }

                return Result.Success(release)
            } else {
                return Result.Error
            }
        }
    }
}