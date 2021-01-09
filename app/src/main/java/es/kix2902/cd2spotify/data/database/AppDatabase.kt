package es.kix2902.cd2spotify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import es.kix2902.cd2spotify.data.models.Release

@Database(entities = [Release::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun releaseDao(): ReleaseDao
}