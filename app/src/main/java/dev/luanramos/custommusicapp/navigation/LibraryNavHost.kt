package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.presentation.MusicPlaybackViewModel
import dev.luanramos.custommusicapp.ui.library.AlbumDetailsScreen
import dev.luanramos.custommusicapp.ui.library.LibraryPlayerScreen
import dev.luanramos.custommusicapp.ui.library.LibraryScreen

@Composable
fun LibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicPlaybackViewModel = hiltViewModel()
    val playback = playbackViewModel.playback

    val backStack = remember {
        mutableStateListOf<LibraryDestination>(LibraryDestination.LibraryScreen)
    }

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
                            backStack.add(LibraryDestination.AlbumDetailsScreen)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.LibraryPlayerScreen -> NavEntry(key) {
                    LibraryPlayerScreen(
                        playback = playback,
                        onPlayerMenuViewAlbum = {
                            backStack.add(LibraryDestination.AlbumDetailsScreen)
                        },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is LibraryDestination.AlbumDetailsScreen -> NavEntry(key) {
                    AlbumDetailsScreen(
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        modifier = modifier
    )
}
