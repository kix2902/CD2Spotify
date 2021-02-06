package es.kix2902.cd2spotify.data.database

import androidx.room.Dao
import androidx.room.Query
import es.kix2902.cd2spotify.data.models.Release

@Dao
interface ReleaseDao : BaseDao<Release> {

    @Query("SELECT * FROM `releases` WHERE barcode LIKE :barcode")
    suspend fun findByBarcode(barcode: String): Release?

    @Query("SELECT * FROM `releases`")
    suspend fun findAll(): List<Release>
}