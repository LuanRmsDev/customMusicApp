package dev.luanramos.custommusicapp.presentation

import dev.luanramos.custommusicapp.domain.TrackPlaybackState
import dev.luanramos.custommusicapp.domain.model.Music

data class MusicUiState(
    val playbackState: TrackPlaybackState = TrackPlaybackState(),
    val isLoading: Boolean = false,
    val songsList: List<Music> = emptyList(),
)
