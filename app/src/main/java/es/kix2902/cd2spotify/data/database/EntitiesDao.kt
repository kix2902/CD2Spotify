package es.kix2902.cd2spotify.data.database

import androidx.room.Dao
import es.kix2902.cd2spotify.data.models.Musicbrainz

@Dao
interface ReleaseDao : BaseDao<Musicbrainz.Release>

@Dao
interface ArtistDao : BaseDao<Musicbrainz.Artist>

@Dao
interface ReleaseArtistDao : BaseDao<Musicbrainz.ReleaseArtist>