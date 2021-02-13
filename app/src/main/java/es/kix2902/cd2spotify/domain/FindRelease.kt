package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Musicbrainz
import kotlinx.coroutines.CoroutineScope

class FindRelease(
    scope: CoroutineScope,
    private val dbRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository
) : UseCase<Musicbrainz.ReleaseWithArtists, String>(scope) {

    override suspend fun run(params: String): Result {
        var release = dbRepository.searchBarcode(params)

        if (release != null) {
            return Result.Success(release)

        } else {
            val success = networkRepository.findReleaseByBarcode(params)

            if (success) {
                release = dbRepository.searchBarcode(params)

                return if (release != null) {
                    Result.Success(release)
                } else {
                    Result.Error
                }

            } else {
                return Result.Error
            }
        }
    }
}