package dev.luanramos.custommusicapp.data.local

import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import dev.luanramos.custommusicapp.domain.model.Music
import javax.inject.Inject
import javax.inject.Singleton

interface TrackPlayRecorder {
    /** Bumps play count and last-played time. */
    suspend fun recordPlay(track: Music)
}

@Singleton
class RoomTrackPlayRecorder @Inject constructor(
    private val musicLibraryDao: MusicLibraryDao,
) : TrackPlayRecorder {
    override suspend fun recordPlay(track: Music) {
        musicLibraryDao.recordPlay(track)
    }
}
