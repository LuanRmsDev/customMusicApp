package dev.luanramos.custommusicapp.data.player

import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FakeTrackPlaybackController : TrackPlaybackController {
    private val _state = MutableStateFlow(TrackPlaybackState())
    override val state = _state.asStateFlow()

    private const val DemoDurationMs = 220_000L

    override fun play(track: Music) {
        _state.value = TrackPlaybackState(
            currentTrack = track,
            isPlaying = true,
            isBuffering = false,
            errorMessage = null,
            positionMs = 86_000L,
            durationMs = DemoDurationMs
        )
    }

    override fun pause() {
        _state.update { it.copy(isPlaying = false) }
    }

    override fun resume() {
        _state.update { it.copy(isPlaying = true) }
    }

    override fun stop() {
        _state.value = TrackPlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        _state.update {
            val d = it.durationMs
            val max = if (d > 0) d else Long.MAX_VALUE
            it.copy(positionMs = positionMs.coerceIn(0L, max))
        }
    }

    override fun skipToPrevious() {
        _state.update { it.copy(positionMs = 0L) }
    }

    override fun skipToNext() {
        // No queue in fake
    }

    override fun release() {
        // No native resources
    }
}
