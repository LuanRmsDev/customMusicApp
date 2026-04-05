package dev.luanramos.custommusicapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.luanramos.custommusicapp.data.local.db.entity.CatalogListEntryEntity
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

    @Transaction
    suspend fun upsertTrackPreservingPlayStats(remote: TrackEntity) {
        val existing = getTrack(remote.id)
        if (existing == null) {
            insertTrack(
                remote.copy(
                    playCount = 0,
                    lastPlayedAt = null,
                ),
            )
        } else {
            updateTrack(
                existing.copy(
                    title = remote.title,
                    artist = remote.artist,
                    songUrl = remote.songUrl,
                    artworkUrl30 = remote.artworkUrl30,
                    artworkUrl60 = remote.artworkUrl60,
                    artworkUrl100 = remote.artworkUrl100,
                    artworkUrl160 = remote.artworkUrl160,
                    artworkUrl600 = remote.artworkUrl600,
                ),
            )
        }
    }

    @Transaction
    suspend fun recordPlay(music: Music) {
        upsertTrackPreservingPlayStats(music.toRemoteTrackEntity())
        bumpPlayCount(music.id, System.currentTimeMillis())
    }

    @Query("DELETE FROM catalog_list_entries WHERE listKey = :listKey")
    suspend fun clearCatalogList(listKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogEntry(entry: CatalogListEntryEntity)

    @Transaction
    suspend fun persistCatalogPage(
        listKey: String,
        startPosition: Int,
        clearListWhenStartingAtZero: Boolean,
        tracks: List<Music>,
    ) {
        if (clearListWhenStartingAtZero && startPosition == 0) {
            clearCatalogList(listKey)
        }
        tracks.forEachIndexed { i, music ->
            upsertTrackPreservingPlayStats(music.toRemoteTrackEntity())
            insertCatalogEntry(
                CatalogListEntryEntity(
                    listKey = listKey,
                    position = startPosition + i,
                    trackId = music.id,
                ),
            )
        }
    }

    @Query(
        """
        SELECT t.* FROM tracks t
        INNER JOIN catalog_list_entries c ON c.trackId = t.id AND c.listKey = :listKey
        WHERE c.position >= :fromInclusive AND c.position < :toExclusive
        ORDER BY c.position ASC
        """,
    )
    suspend fun getCatalogTracksRange(
        listKey: String,
        fromInclusive: Int,
        toExclusive: Int,
    ): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM catalog_list_entries WHERE listKey = :listKey")
    suspend fun countCatalogEntries(listKey: String): Int

    @Query(
        """
        SELECT * FROM tracks WHERE lastPlayedAt IS NOT NULL
        ORDER BY lastPlayedAt DESC
        LIMIT :limit OFFSET :offset
        """,
    )
    suspend fun getRecentTracks(limit: Int, offset: Int): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM tracks WHERE lastPlayedAt IS NOT NULL")
    suspend fun countRecentTracks(): Int

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun countAllTracks(): Int
}
