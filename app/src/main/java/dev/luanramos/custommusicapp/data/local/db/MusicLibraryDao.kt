package dev.luanramos.custommusicapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.luanramos.custommusicapp.data.local.db.entity.TrackEntity
import dev.luanramos.custommusicapp.domain.model.Music

@Dao
interface MusicLibraryDao {

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrack(id: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTrack(entity: TrackEntity)

    @Update
    suspend fun updateTrack(entity: TrackEntity)

    @Query(
        """
        UPDATE tracks SET playCount = playCount + 1, lastPlayedAt = :at
        WHERE id = :id
        """,
    )
    suspend fun bumpPlayCount(id: String, at: Long)

    /**
     * Bumps [playCount] and sets [TrackEntity.lastPlayedAt] for an **existing** row.
     * Does not insert or change metadata, paths, or artwork URLs — use [saveSong] to persist a track first.
     * If no row exists for [Music.id], the update affects zero rows.
     */
    suspend fun recordPlay(music: Music) {
        bumpPlayCount(music.id, System.currentTimeMillis())
    }

    @Transaction
    suspend fun saveSong(music: Music) {
        val now = System.currentTimeMillis()
        val row = music.toPersistedTrackEntity().copy(lastPlayedAt = now)
        val existing = getTrack(music.id)
        if (existing == null) {
            insertTrack(row)
        } else {
            updateTrack(
                row.copy(
                    localAudioPath = music.localAudioPath ?: existing.localAudioPath,
                    localArtworkPath = music.localArtworkPath ?: existing.localArtworkPath,
                    albumTitle = row.albumTitle ?: existing.albumTitle,
                    amgAlbumId = row.amgAlbumId ?: existing.amgAlbumId,
                    collectionId = row.collectionId ?: existing.collectionId,
                    artworkUrl30 = row.artworkUrl30 ?: existing.artworkUrl30,
                    artworkUrl60 = row.artworkUrl60 ?: existing.artworkUrl60,
                    artworkUrl100 = row.artworkUrl100 ?: existing.artworkUrl100,
                    artworkUrl160 = row.artworkUrl160 ?: existing.artworkUrl160,
                    artworkUrl600 = row.artworkUrl600 ?: existing.artworkUrl600,
                ),
            )
        }
    }

    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()

    @Query(
        """
        SELECT * FROM tracks WHERE lastPlayedAt IS NOT NULL
        ORDER BY lastPlayedAt DESC
        LIMIT :limit OFFSET :offset
        """,
    )
    suspend fun getRecentTracks(limit: Int, offset: Int): List<TrackEntity>
}
