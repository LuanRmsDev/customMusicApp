package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.ui.androidauto.CarAlbumListScreen
import dev.luanramos.custommusicapp.ui.androidauto.CarBrowseScreen
import dev.luanramos.custommusicapp.ui.androidauto.CarPlayerScreen
import kotlinx.coroutines.flow.map

@Composable
fun CarLibraryNavHost(modifier: Modifier = Modifier) {
    val musicViewModel: MusicViewModel = hiltViewModel()
    val currentTrackId by remember(musicViewModel) {
        musicViewModel.uiState.map { it.playbackState.currentTrack?.id }
    }.collectAsStateWithLifecycle(
        initialValue = musicViewModel.uiState.value.playbackState.currentTrack?.id,
    )
    val album by musicViewModel.albumScreenState.collectAsStateWithLifecycle()
    val mocked by musicViewModel.mockedDataState.collectAsStateWithLifecycle()
    val backStack = rememberLibraryBackStack(saveKey = "android_auto_nav")

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen,
                is LibraryDestination.WatchSongsList -> NavEntry(key) {
                    CarBrowseScreen(
                        songs = mocked.songs,
                        currentTrackId = currentTrackId,
                        isCatalogLoading = false,
                        onRetryCatalog = { },
                        onSongClick = { song ->
                            musicViewModel.playTrack(song, mocked.songs)
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    CarPlayerScreen(
                        viewModel = musicViewModel,
                        onBackToMusic = { backStack.removeLastOrNull() },
                        onOpenAlbum = {
                            musicViewModel.uiState.value.playbackState.currentTrack?.let { t ->
                                musicViewModel.loadAlbumFromTrack(t)
                            }
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    CarAlbumListScreen(
                        albumTitle = album.albumTitle,
                        tracks = album.tracks,
                        isLoading = album.isLoading,
                        currentTrackId = currentTrackId,
                        onBack = { backStack.removeLastOrNull() },
                        onTrackClick = { song ->
                            musicViewModel.playTrack(song, album.tracks)
                            while (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        modifier = modifier
    )
}
