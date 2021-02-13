package es.kix2902.cd2spotify.domain

import es.kix2902.cd2spotify.data.NetworkRepository
import es.kix2902.cd2spotify.data.models.Musicbrainz
import es.kix2902.cd2spotify.data.models.Spotify
import es.kix2902.cd2spotify.helpers.hasInfo
import kotlinx.coroutines.CoroutineScope

class FindSpotifyAlbum(scope: CoroutineScope, private val networkRepository: NetworkRepository) :
    UseCase<Spotify.Album, Musicbrainz.ReleaseWithArtists>(scope) {

    override suspend fun run(params: Musicbrainz.ReleaseWithArtists): Result {
        var album: Spotify.Album? = null

        if (params.hasInfo()) {
            album = networkRepository.findSpotifyByRelease(params)

            if (album == null) {
                album = networkRepository.findSpotifyByQuery(params)
            }
        }

        if (album == null) {
            album = networkRepository.findSpotifyByBarcode(params)
        }

        if (album != null) {
            val tracks = networkRepository.findSpotifyTracks(album.id)
            album.tracks = tracks
            return Result.Success(album)

        } else {
            return Result.Error
        }
    }
}