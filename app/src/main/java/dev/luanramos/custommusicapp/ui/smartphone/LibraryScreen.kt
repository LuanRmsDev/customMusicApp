package dev.luanramos.custommusicapp.ui.smartphone

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.presentation.PreviewMusicRepository
import dev.luanramos.custommusicapp.ui.components.LibrarySearchField
import dev.luanramos.custommusicapp.ui.components.LibrarySongRow
import dev.luanramos.custommusicapp.ui.components.LibraryTopBar
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onOpenPlayer: () -> Unit,
    onLibraryMenuViewAlbum: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var menuSongId by rememberSaveable { mutableStateOf<String?>(null) }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    val songsById = remember(ui.songsList, LibraryMockedData.sampleDisplayAlbumTracks) {
        (ui.songsList + LibraryMockedData.sampleDisplayAlbumTracks).associateBy { it.id }
    }
    val menuSong = remember(menuSongId) { menuSongId?.let { songsById[it] } }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            searchFocusRequester.requestFocus()
        } else {
            keyboard?.hide()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            LibraryTopBar(
                title = stringResource(R.string.songs_screen_title),
                modifier = Modifier.statusBarsPadding(),
                isSearchActive = isSearchActive,
                onSearchToggleClick = {
                    if (isSearchActive) {
                        searchQuery = ""
                        viewModel.submitSearchQuery("")
                        keyboard?.hide()
                    }
                    isSearchActive = !isSearchActive
                }
            )
            AnimatedVisibility(
                visible = isSearchActive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LibrarySearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearchSubmit = {
                        keyboard?.hide()
                        viewModel.submitSearchQuery(searchQuery)
                    },
                    focusRequester = searchFocusRequester,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            val retryBrowse: () -> Unit = {
                if (isSearchActive && searchQuery.isNotBlank()) {
                    viewModel.submitSearchQuery(searchQuery)
                } else {
                    viewModel.retryLoadLibrary()
                }
            }
            when {
                ui.isLoading && ui.songsList.isEmpty() ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }

                ui.songsList.isEmpty() ->
                    NoInternetScreen(
                        onRetry = retryBrowse,
                        modifier = Modifier
                            .weight(1f)
                            .navigationBarsPadding(),
                    )

                else ->
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .navigationBarsPadding(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
                    ) {
                        items(
                            items = ui.songsList,
                            key = { it.id }
                        ) { song ->
                            LibrarySongRow(
                                title = song.title,
                                artist = song.artist,
                                track = song,
                                onRowClick = {
                                    viewModel.playTrack(song)
                                    onOpenPlayer()
                                },
                                onMoreClick = { menuSongId = song.id }
                            )
                        }
                    }
            }
        }

        menuSong?.let { song ->
            PlayerMenuBottomSheet(
                onDismiss = { menuSongId = null },
                songTitle = song.title,
                artistName = song.artist,
                onViewAlbumClick = { onLibraryMenuViewAlbum(song) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun LibraryScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    CustomMusicAppTheme {
        LibraryScreen(
            viewModel = vm,
            onOpenPlayer = {},
            onLibraryMenuViewAlbum = {}
        )
    }
}
