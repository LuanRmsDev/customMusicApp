package dev.luanramos.custommusicapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import javax.inject.Inject
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

    //TODO: The Android Auto and Watch will observe only the mocked data for know, as they don't have a search feature in designs
    private val _mockedDataState = MutableStateFlow(LibraryMockedData)
    val mockedDataState = _mockedDataState.asStateFlow()

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
                val songs = browseListLastPlayedOrPopular()
                _uiState.update { it.copy(songsList = songs) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun browseListLastPlayedOrPopular(): List<Music> {
        val recent = musicRepository.getLastPlayedSongs(limit = BROWSE_PAGE_SIZE, offset = 0)
        return recent.ifEmpty {
            musicRepository.getPopularSongs(limit = BROWSE_PAGE_SIZE)
        }
    }

    /** Re-runs the default catalog (recent first, else popular). */
    fun retryLoadLibrary() {
        loadCatalog()
    }

    /**
     * Loads the browse list for the current search field value. Call when the user submits search
     * (search button or IME search), not on each keystroke. Empty [query] reloads the default catalog
     * (recently played from disk when available, otherwise popular from the API).
     */
    fun submitSearchQuery(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val q = query.trim()
                val songs =
                    if (q.isEmpty()) {
                        browseListLastPlayedOrPopular()
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
        viewModelScope.launch {
            playback.play(music)
            musicRepository.saveSong(music)
        }
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
    }
}
