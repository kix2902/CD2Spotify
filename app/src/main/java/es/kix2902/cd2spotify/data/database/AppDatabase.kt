package es.kix2902.cd2spotify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import es.kix2902.cd2spotify.data.models.Musicbrainz

@Database(
    entities = [Musicbrainz.Release::class, Musicbrainz.Artist::class, Musicbrainz.ReleaseArtist::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun releaseDao(): ReleaseDao
    abstract fun artistDao(): ArtistDao
    abstract fun releaseArtistDao(): ReleaseArtistDao
    abstract fun releaseWithArtistDao(): ReleaseWithArtistDao
}