package dev.luanramos.custommusicapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.luanramos.custommusicapp.data.local.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicLibraryDao(): MusicLibraryDao
}
