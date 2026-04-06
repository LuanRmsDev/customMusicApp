package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.ui.smartphone.AlbumDisplayScreen
import dev.luanramos.custommusicapp.ui.smartphone.LibraryPlayerScreen
import dev.luanramos.custommusicapp.ui.smartphone.LibraryScreen

@Composable
fun SmartphoneLibraryNavHost(modifier: Modifier = Modifier) {
    val musicViewModel: MusicViewModel = hiltViewModel()
    val album by musicViewModel.albumScreenState.collectAsStateWithLifecycle()
    val backStack = rememberLibraryBackStack(saveKey = "smartphone_nav")

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen,
                is LibraryDestination.WatchSongsList -> NavEntry(key) {
                    LibraryScreen(
                        viewModel = musicViewModel,
                        onOpenPlayer = {
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        onLibraryMenuViewAlbum = { song ->
                            musicViewModel.loadAlbumFromTrack(song)
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    LibraryPlayerScreen(
                        viewModel = musicViewModel,
                        onPlayerMenuViewAlbum = {
                            musicViewModel.uiState.value.playbackState.currentTrack?.let { t ->
                                musicViewModel.loadAlbumFromTrack(t)
                            }
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    AlbumDisplayScreen(
                        albumTitle = album.albumTitle,
                        artistName = album.artistName,
                        tracks = album.tracks,
                        isLoading = album.isLoading,
                        onBack = { backStack.removeLastOrNull() },
                        onTrackClick = { song ->
                            musicViewModel.playTrack(song, album.tracks)
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
