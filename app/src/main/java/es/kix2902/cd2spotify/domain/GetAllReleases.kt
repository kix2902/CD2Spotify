package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.DatabaseRepository
import es.kix2902.cd2spotify.data.models.Release
import kotlinx.coroutines.CoroutineScope

class GetAllReleases(scope: CoroutineScope, private val dbRepository: DatabaseRepository) :
    UseCase<List<Release>, UseCase.None>(scope) {

    override suspend fun run(params: None): Result {
        val releases = dbRepository.getAllReleases()
        return Result.Success(releases)
    }
}