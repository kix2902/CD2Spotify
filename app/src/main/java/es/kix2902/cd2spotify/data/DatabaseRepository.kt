package es.kix2902.cd2spotify.data

import android.content.Context
import androidx.room.Room
import es.kix2902.cd2spotify.data.database.AppDatabase
import es.kix2902.cd2spotify.data.models.Musicbrainz
import es.kix2902.cd2spotify.helpers.SingletonHolder

class DatabaseRepository private constructor(context: Context) {

    companion object : SingletonHolder<DatabaseRepository, Context>(::DatabaseRepository)

    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "cd2spotify")
        .fallbackToDestructiveMigration()
        .build()

    suspend fun insertRelease(release: Musicbrainz.Release) {
        db.releaseDao().insert(release)
    }

    suspend fun insertArtist(artist: Musicbrainz.Artist) {
        db.artistDao().insert(artist)
    }

    suspend fun insertReleaseArtist(releaseArtist: Musicbrainz.ReleaseArtist) {
        db.releaseArtistDao().insert(releaseArtist)
    }

    suspend fun getAllReleases(): List<Musicbrainz.ReleaseWithArtists> {
        return db.releaseWithArtistDao().getReleasesWithArtists()
    }

    suspend fun searchBarcode(barcode: String): Musicbrainz.ReleaseWithArtists? {
        return db.releaseWithArtistDao().getReleasesWithArtistsByBarcode(barcode)
    }
}