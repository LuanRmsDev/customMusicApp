package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.presentation.MusicPlaybackViewModel
import dev.luanramos.custommusicapp.ui.library.AlbumDisplayScreen
import dev.luanramos.custommusicapp.ui.library.LibraryPlayerScreen
import dev.luanramos.custommusicapp.ui.library.LibraryScreen

private val libraryBackStackSaver =
    listSaver<SnapshotStateList<LibraryDestination>, String>(
        save = { stack -> stack.map { it.toSaveKey() } },
        restore = { keys ->
            if (keys.isEmpty()) {
                mutableStateListOf(LibraryDestination.LibraryScreen)
            } else {
                mutableStateListOf<LibraryDestination>().apply {
                    addAll(keys.map { it.toLibraryDestination() })
                }
            }
        }
    )

@Composable
fun LibraryNavHost(modifier: Modifier = Modifier) {
    val playbackViewModel: MusicPlaybackViewModel = hiltViewModel()
    val playback = playbackViewModel.playback

    val backStack = rememberSaveable(saver = libraryBackStackSaver) {
        mutableStateListOf(LibraryDestination.LibraryScreen)
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
