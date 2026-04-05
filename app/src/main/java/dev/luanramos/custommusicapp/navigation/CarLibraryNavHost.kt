package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.ui.androidauto.CarAlbumListScreen
import dev.luanramos.custommusicapp.ui.androidauto.CarBrowseScreen
import dev.luanramos.custommusicapp.ui.androidauto.CarPlayerScreen

@Composable
fun CarLibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicViewModel = hiltViewModel()
    val playback = playbackViewModel.playback
    val playbackState by playback.state.collectAsStateWithLifecycle()
    val backStack = rememberLibraryBackStack(saveKey = "android_auto_nav")

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is LibraryDestination.LibraryScreen,
                is LibraryDestination.WatchSongsList -> NavEntry(key) {
                    CarBrowseScreen(
                        songs = LibraryMockedData.songs,
                        currentTrackId = playbackState.currentTrack?.id,
                        onSongClick = { song ->
                            playback.play(song)
                            backStack.add(LibraryDestination.LibraryPlayerScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    CarPlayerScreen(
                        playback = playback,
                        onBackToMusic = { backStack.removeLastOrNull() },
                        onOpenAlbum = {
                            backStack.add(LibraryDestination.AlbumDisplayScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDisplayScreen -> NavEntry(key) {
                    CarAlbumListScreen(
                        albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
                        tracks = LibraryMockedData.sampleDisplayAlbumTracks,
                        currentTrackId = playbackState.currentTrack?.id,
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
