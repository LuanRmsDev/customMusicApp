package dev.luanramos.custommusicapp.domain

import kotlinx.coroutines.flow.StateFlow

data class TrackPlaybackState(
    val currentTrack: Music? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
)

interface TrackPlaybackController {
    val state: StateFlow<TrackPlaybackState>

    fun play(track: Music)

    fun pause()

    fun resume()

    fun stop()

    /** Release native resources */
    fun release()
}
