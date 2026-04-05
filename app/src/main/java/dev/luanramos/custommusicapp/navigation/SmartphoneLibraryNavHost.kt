package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.presentation.MusicPlaybackViewModel
import dev.luanramos.custommusicapp.ui.smartphone.AlbumDisplayScreen
import dev.luanramos.custommusicapp.ui.smartphone.LibraryPlayerScreen
import dev.luanramos.custommusicapp.ui.smartphone.LibraryScreen

@Composable
fun SmartphoneLibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicPlaybackViewModel = hiltViewModel()
    val playback = playbackViewModel.playback
    val backStack = rememberLibraryBackStack()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen -> NavEntry(key) {
                    LibraryScreen(
                        playback = playback,
                        onOpenPlayer = {
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        onLibraryMenuViewAlbum = { _ ->
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    LibraryPlayerScreen(
                        playback = playback,
                        onPlayerMenuViewAlbum = {
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    AlbumDisplayScreen(
                        albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
                        artistName = LibraryMockedData.sampleDisplayAlbumArtist,
                        tracks = LibraryMockedData.sampleDisplayAlbumTracks,
                        onBack = { backStack.removeLastOrNull() },
                        onTrackClick = { song ->
                            playback.play(song)
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
