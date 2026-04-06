package dev.luanramos.custommusicapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _libraryScrollToTop = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val libraryScrollToTop: SharedFlow<Unit> = _libraryScrollToTop.asSharedFlow()

    private val _repeatOne = MutableStateFlow(false)
    val repeatOne: StateFlow<Boolean> = _repeatOne.asStateFlow()

    private val _albumScreenState = MutableStateFlow(AlbumScreenState())
    val albumScreenState: StateFlow<AlbumScreenState> = _albumScreenState.asStateFlow()

    private var playQueue: List<Music> = emptyList()

    //TODO: The Android Auto and Watch will observe only the mocked data for know, as they don't have a search feature in designs
    private val _mockedDataState = MutableStateFlow(LibraryMockedData)
    val mockedDataState = _mockedDataState.asStateFlow()

    init {
        viewModelScope.launch {
            playback.state.collect { pb ->
                _uiState.update { it.copy(playbackState = pb) }
            }
        }
        loadCatalog(emitScrollToTop = false)
    }

    private fun loadCatalog(emitScrollToTop: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val songs = browseListLastPlayedOrPopular()
                _uiState.update { it.copy(songsList = songs, activeSearchQuery = null) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                if (emitScrollToTop) {
                    _libraryScrollToTop.tryEmit(Unit)
                }
            }
        }
    }

    private suspend fun browseListLastPlayedOrPopular(): List<Music> {
        val recent = musicRepository.getLastPlayedSongs(limit = BROWSE_PAGE_SIZE, offset = 0)
        return recent.ifEmpty {
            musicRepository.getPopularSongs(limit = MusicRepository.MAX_ITUNES_SEARCH_LIMIT)
        }
    }

    /** Re-runs the default catalog (recent first, else popular). */
    fun retryLoadLibrary() {
        loadCatalog(emitScrollToTop = true)
    }

    /**
     * Pull-to-refresh: reloads the default catalog when [query] is blank, otherwise repeats the search.
     */
    fun refreshBrowse(query: String) {
        val q = query.trim()
        if (q.isEmpty()) {
            loadCatalog(emitScrollToTop = true)
        } else {
            submitSearchQuery(q)
        }
    }

    /**
     * Loads album tracks via iTunes lookup ([Music.amgAlbumId] preferred, else [Music.collectionId]).
     * On failure or missing ids, falls back to a single-track “album” using [anchor].
     */
    fun loadAlbumFromTrack(anchor: Music) {
        viewModelScope.launch {
            _albumScreenState.value =
                AlbumScreenState(
                    albumTitle = anchor.albumTitle.orEmpty(),
                    artistName = anchor.artist,
                    tracks = emptyList(),
                    isLoading = true,
                )
            val detail = musicRepository.getAlbumDetail(anchor)
            _albumScreenState.value =
                if (detail != null) {
                    AlbumScreenState(
                        albumTitle = detail.title,
                        artistName = detail.artistName,
                        tracks = detail.tracks,
                        isLoading = false,
                    )
                } else {
                    AlbumScreenState(
                        albumTitle = anchor.albumTitle ?: anchor.title,
                        artistName = anchor.artist,
                        tracks = listOf(anchor),
                        isLoading = false,
                    )
                }
        }
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
                        musicRepository.searchSong(
                            searchTerm = q,
                            limit = MusicRepository.MAX_ITUNES_SEARCH_LIMIT,
                        )
                    }
                _uiState.update {
                    it.copy(
                        songsList = songs,
                        activeSearchQuery =
                            if (q.isNotEmpty() && songs.isEmpty()) {
                                q
                            } else {
                                null
                            },
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                _libraryScrollToTop.tryEmit(Unit)
            }
        }
    }

    fun playTrack(music: Music, queue: List<Music> = emptyList()) {
        playQueue = resolvePlayQueue(queue)
        viewModelScope.launch {
            playback.play(music)
            musicRepository.saveSong(music)
        }
    }

    private fun resolvePlayQueue(queue: List<Music>): List<Music> {
        if (queue.isNotEmpty()) return queue
        val fromUi = _uiState.value.songsList
        if (fromUi.isNotEmpty()) return fromUi
        return _mockedDataState.value.songs
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
        val pb = playback.state.value
        val current = pb.currentTrack ?: return
        if (pb.positionMs > RESTART_SONG_POSITION_THRESHOLD_MS) {
            playback.seekTo(0L)
            return
        }
        if (playQueue.isEmpty()) {
            playback.skipToPrevious()
            return
        }
        val idx = playQueue.indexOfFirst { it.id == current.id }
        if (idx < 0) return
        val prevIdx = if (idx > 0) idx - 1 else playQueue.lastIndex
        val prev = playQueue[prevIdx]
        viewModelScope.launch {
            playback.play(prev)
            musicRepository.saveSong(prev)
        }
    }

    fun skipToNext() {
        val current = playback.state.value.currentTrack ?: return
        if (playQueue.isEmpty()) {
            playback.skipToNext()
            return
        }
        val idx = playQueue.indexOfFirst { it.id == current.id }
        if (idx < 0) return
        val next = playQueue[(idx + 1) % playQueue.size]
        viewModelScope.launch {
            playback.play(next)
            musicRepository.saveSong(next)
        }
    }

    fun toggleRepeatOne() {
        val next = !_repeatOne.value
        _repeatOne.value = next
        playback.setRepeatOne(next)
    }

    companion object {
        const val BROWSE_PAGE_SIZE: Int = 20
        private const val RESTART_SONG_POSITION_THRESHOLD_MS = 3_000L
    }
}
