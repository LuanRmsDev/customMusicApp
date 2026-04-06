package dev.luanramos.custommusicapp.domain.model

data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val songUrl: String? = null,
    val artwork: MusicArtwork? = null,
    val albumTitle: String? = null,
    val amgAlbumId: Long? = null,
    val collectionId: Long? = null,
    val playCount: Int = 0,
    val lastPlayedAt: Long? = null,
    val localAudioPath: String? = null,
    val localArtworkPath: String? = null,
)
