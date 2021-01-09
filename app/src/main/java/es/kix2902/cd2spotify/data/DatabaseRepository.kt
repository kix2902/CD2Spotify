package es.kix2902.cd2spotify.data

import android.content.Context
import androidx.room.Room
import es.kix2902.cd2spotify.data.database.AppDatabase
import es.kix2902.cd2spotify.data.models.Release
import es.kix2902.cd2spotify.helpers.SingletonHolder

class DatabaseRepository private constructor(context: Context) {

    companion object : SingletonHolder<DatabaseRepository, Context>(::DatabaseRepository)

    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "cd2spotify").build()

    suspend fun insertRelease(release: Release) {
        db.releaseDao().insert(release)
    }

    suspend fun searchBarcode(barcode: String): Release? {
        return db.releaseDao().findByBarcode(barcode)
    }
}