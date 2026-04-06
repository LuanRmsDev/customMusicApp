package dev.luanramos.custommusicapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val albumTitle: String?,
    val amgAlbumId: Long?,
    val collectionId: Long?,
    val songUrl: String?,
    val artworkUrl30: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val artworkUrl160: String?,
    val artworkUrl600: String?,
    val playCount: Int = 0,
    val lastPlayedAt: Long? = null,
    val localAudioPath: String? = null,
    val localArtworkPath: String? = null,
)
