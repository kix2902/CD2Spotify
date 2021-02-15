package es.kix2902.cd2spotify.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import es.kix2902.cd2spotify.data.models.Musicbrainz

@Dao
interface ReleaseWithArtistDao {
    @Transaction
    @Query("SELECT * FROM `release`")
    fun getReleasesWithArtists(): List<Musicbrainz.ReleaseWithArtists>

    @Transaction
    @Query("SELECT * FROM `release` WHERE `release`.barcode = :barcode")
    fun getReleasesWithArtistsByBarcode(barcode: String): Musicbrainz.ReleaseWithArtists?
}