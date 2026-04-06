package dev.luanramos.custommusicapp.presentation

import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackState
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicViewModelTest {

    private val musicRepository: MusicRepository = mockk(relaxed = true)
    private val playbackState = MutableStateFlow(TrackPlaybackState())
    private val playback: TrackPlaybackController =
        mockk(relaxed = true) {
            every { state } returns playbackState
        }

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial catalog loads popular when recent is empty`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            val popular =
                listOf(
                    Music(id = "1", title = "Pop", artist = "A", songUrl = "https://p.example/1.m4a"),
                )
            coEvery { musicRepository.getPopularSongs(any()) } returns popular

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            assertEquals(popular, viewModel.uiState.value.songsList)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.activeSearchQuery)
        }

    @Test
    fun `submitSearchQuery with blank reloads catalog`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.submitSearchQuery("  ")
            advanceUntilIdle()

            coVerify(atLeast = 2) { musicRepository.getLastPlayedSongs(any(), any()) }
        }

    @Test
    fun `submitSearchQuery with term calls search and sets activeSearchQuery when empty`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()
            coEvery { musicRepository.searchSong("xyz", any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.submitSearchQuery("xyz")
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.songsList.isEmpty())
            assertEquals("xyz", viewModel.uiState.value.activeSearchQuery)
            coVerify { musicRepository.searchSong("xyz", MusicRepository.MAX_ITUNES_SEARCH_LIMIT) }
        }

    @Test
    fun `refreshBrowse with blank reloads catalog`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.refreshBrowse("  ")
            advanceUntilIdle()

            coVerify(atLeast = 2) { musicRepository.getLastPlayedSongs(any(), any()) }
        }

    @Test
    fun `refreshBrowse with query triggers search`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()
            coEvery { musicRepository.searchSong("q", any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.refreshBrowse(" q ")
            advanceUntilIdle()

            coVerify { musicRepository.searchSong("q", MusicRepository.MAX_ITUNES_SEARCH_LIMIT) }
        }

    @Test
    fun `playTrack delegates to playback and saveSong`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()

            val track = Music(id = "t1", title = "T", artist = "A", songUrl = "https://p.example/x.m4a")
            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.playTrack(track)
            advanceUntilIdle()

            verify { playback.play(track) }
            coVerify { musicRepository.saveSong(track) }
        }

    @Test
    fun `pause delegates to playback`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.pause()

            verify { playback.pause() }
        }

    @Test
    fun `toggleRepeatOne updates playback`() =
        runTest(testDispatcher) {
            coEvery { musicRepository.getLastPlayedSongs(any(), any()) } returns emptyList()
            coEvery { musicRepository.getPopularSongs(any()) } returns emptyList()

            val viewModel = MusicViewModel(musicRepository, playback)
            advanceUntilIdle()

            viewModel.toggleRepeatOne()

            verify { playback.setRepeatOne(true) }
        }
}
