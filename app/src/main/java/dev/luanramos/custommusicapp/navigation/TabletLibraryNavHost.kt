package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.ui.tablet.TabletAlbumScreen
import dev.luanramos.custommusicapp.ui.tablet.TabletHomeScreen
import dev.luanramos.custommusicapp.ui.tablet.TabletPlayerScreen

@Composable
fun TabletLibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicViewModel = hiltViewModel()
    val playback = playbackViewModel.playback
    val backStack = rememberLibraryBackStack(saveKey = "tablet_nav")

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen,
                is LibraryDestination.WatchSongsList -> NavEntry(key) {
                    TabletHomeScreen(
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
                    TabletPlayerScreen(
                        playback = playback,
                        onPlayerMenuViewAlbum = {
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    TabletAlbumScreen(
                        albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
                        artistName = LibraryMockedData.sampleDisplayAlbumArtist,
                        tracks = LibraryMockedData.sampleDisplayAlbumTracks,
                        onBack = { backStack.removeLastOrNull() },
                        onTrackClick = { song ->
                            playback.play(song)
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
