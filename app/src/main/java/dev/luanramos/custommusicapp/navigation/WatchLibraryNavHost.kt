package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.ui.watch.WatchAlbumScreen
import dev.luanramos.custommusicapp.ui.watch.WatchLibraryScreen
import dev.luanramos.custommusicapp.ui.watch.WatchMainNavScreen
import dev.luanramos.custommusicapp.ui.watch.WatchPlayerScreen

/**
 * Wear OS flow: **Music** hub (time + Now playing / Albums / Songs) → destinations; **swipe down**
 * on the player clears the stack to the hub (Figma navigation note).
 */
@Composable
fun WatchLibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicViewModel = hiltViewModel()
    val playback = playbackViewModel.playback
    val backStack = rememberLibraryBackStack(saveKey = "smartwatch_nav")

    fun popToMusicHub() {
        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen -> NavEntry(key) {
                    WatchMainNavScreen(
                        onNowPlayingClick = {
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        onAlbumsClick = {
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        onSongsClick = {
                            backStack.add(LibraryDestination.WatchSongsList)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.WatchSongsList -> NavEntry(key) {
                    WatchLibraryScreen(
                        playback = playback,
                        onOpenPlayer = {
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    WatchPlayerScreen(
                        playback = playback,
                        onPlayerMenuViewAlbum = {
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        onSwipeDownToMainNav = { popToMusicHub() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    WatchAlbumScreen(
                        albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
                        artistName = LibraryMockedData.sampleDisplayAlbumArtist,
                        tracks = LibraryMockedData.sampleDisplayAlbumTracks,
                        onBack = { backStack.removeLastOrNull() },
                        onTrackClick = { song ->
                            playback.play(song)
                            popToMusicHub()
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
