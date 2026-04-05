package dev.luanramos.custommusicapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playback: TrackPlaybackController,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            playback.state.collect { pb ->
                _uiState.update { it.copy(playbackState = pb) }
            }
        }
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val songs = musicRepository.getPopularSongs(limit = BROWSE_PAGE_SIZE)
                _uiState.update { it.copy(songsList = songs) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /** Re-runs the initial popular catalog load (e.g. after a failed network attempt). */
    fun retryLoadLibrary() {
        loadCatalog()
    }

    /**
     * Updates the on-screen browse list: empty query reloads popular tracks; non-empty runs iTunes search.
     * Debounced to limit API calls while typing.
     */
    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_MS)
                _uiState.update { it.copy(isLoading = true) }
                try {
                    val q = query.trim()
                    val songs =
                        if (q.isEmpty()) {
                            musicRepository.getPopularSongs(BROWSE_PAGE_SIZE)
                        } else {
                            musicRepository.searchSong(searchTerm = q, limit = BROWSE_PAGE_SIZE)
                        }
                    _uiState.update { it.copy(songsList = songs) }
                } finally {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
    }

    fun playTrack(music: Music) {
        playback.play(music)
    }

    fun pause() {
        playback.pause()
    }

    fun resume() {
        playback.resume()
    }

    fun stopPlayback() {
        playback.stop()
    }

    fun seekTo(positionMs: Long) {
        playback.seekTo(positionMs)
    }

    fun skipToPrevious() {
        playback.skipToPrevious()
    }

    fun skipToNext() {
        playback.skipToNext()
    }

    companion object {
        const val BROWSE_PAGE_SIZE: Int = 20
        private const val SEARCH_DEBOUNCE_MS: Long = 320L
    }
}
