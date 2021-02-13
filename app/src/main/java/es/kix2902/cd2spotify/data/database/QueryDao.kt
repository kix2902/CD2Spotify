package es.kix2902.cd2spotify.data.database

import androidx.room.Dao
import androidx.room.Query
import es.kix2902.cd2spotify.data.models.Musicbrainz

@Dao
interface ReleaseWithArtistDao {
    @Query("SELECT * FROM `release`")
    fun getReleasesWithArtists(): List<Musicbrainz.ReleaseWithArtists>

    @Query("SELECT * FROM `release` WHERE `release`.barcode = :barcode")
    fun getReleasesWithArtistsByBarcode(barcode: String): Musicbrainz.ReleaseWithArtists?
}