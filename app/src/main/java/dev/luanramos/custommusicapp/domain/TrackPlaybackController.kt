package dev.luanramos.custommusicapp.domain

import kotlinx.coroutines.flow.StateFlow

data class TrackPlaybackState(
    val currentTrack: Music? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)

interface TrackPlaybackController {
    val state: StateFlow<TrackPlaybackState>

    fun play(track: Music)

    fun pause()

    fun resume()

    fun stop()

    fun seekTo(positionMs: Long)

    fun skipToPrevious()

    fun skipToNext()

    /** Release native resources */
    fun release()
}
