package dev.luanramos.custommusicapp.data.local.db

import dev.luanramos.custommusicapp.data.local.db.entity.TrackEntity
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.model.MusicArtwork

/** Row for remote metadata merge; [playCount] / [lastPlayedAt] are overwritten when preserving stats. */
fun Music.toRemoteTrackEntity(): TrackEntity =
    TrackEntity(
        id = id,
        title = title,
        artist = artist,
        songUrl = songUrl,
        artworkUrl30 = artwork?.url30,
        artworkUrl60 = artwork?.url60,
        artworkUrl100 = artwork?.url100,
        artworkUrl160 = artwork?.url160,
        artworkUrl600 = artwork?.url600,
        playCount = 0,
        lastPlayedAt = null,
        localAudioPath = localAudioPath,
        localArtworkPath = localArtworkPath,
    )

fun TrackEntity.toMusic(): Music {
    val art =
        if (
            !artworkUrl30.isNullOrBlank() ||
            !artworkUrl60.isNullOrBlank() ||
            !artworkUrl100.isNullOrBlank() ||
            !artworkUrl160.isNullOrBlank() ||
            !artworkUrl600.isNullOrBlank()
        ) {
            MusicArtwork(
                url30 = artworkUrl30,
                url60 = artworkUrl60,
                url100 = artworkUrl100,
                url160 = artworkUrl160,
                url600 = artworkUrl600,
            )
        } else {
            null
        }
    return Music(
        id = id,
        title = title,
        artist = artist,
        songUrl = songUrl,
        artwork = art,
        playCount = playCount,
        lastPlayedAt = lastPlayedAt,
        localAudioPath = localAudioPath,
        localArtworkPath = localArtworkPath,
    )
}
