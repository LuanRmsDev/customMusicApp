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

    override fun play(track: Music) {
        _state.value = TrackPlaybackState(
            currentTrack = track,
            isPlaying = true,
            isBuffering = false,
            errorMessage = null
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

    override fun release() {
        // Do Nothing
    }
}