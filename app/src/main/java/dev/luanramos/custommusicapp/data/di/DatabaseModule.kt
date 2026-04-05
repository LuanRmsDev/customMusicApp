package dev.luanramos.custommusicapp.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.luanramos.custommusicapp.data.local.db.MusicDatabase
import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_library.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideMusicLibraryDao(database: MusicDatabase): MusicLibraryDao =
        database.musicLibraryDao()
}
