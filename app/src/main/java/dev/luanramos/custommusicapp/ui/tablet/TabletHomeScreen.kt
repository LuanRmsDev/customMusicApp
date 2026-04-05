package dev.luanramos.custommusicapp.ui.tablet

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.presentation.PreviewMusicRepository
import dev.luanramos.custommusicapp.ui.components.LibrarySearchField
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.components.TabletSongRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun TabletHomeScreen(
    viewModel: MusicViewModel,
    onOpenPlayer: () -> Unit,
    onLibraryMenuViewAlbum: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var menuSongId by rememberSaveable { mutableStateOf<String?>(null) }

    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    val songsById = remember(ui.songsList, LibraryMockedData.sampleDisplayAlbumTracks) {
        (ui.songsList + LibraryMockedData.sampleDisplayAlbumTracks).associateBy { it.id }
    }
    val menuSong = remember(menuSongId) { menuSongId?.let { songsById[it] } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.songs_screen_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 34.sp,
                lineHeight = 40.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp)
        )
        LibrarySearchField(
            query = searchQuery,
            onQueryChange = { q ->
                searchQuery = q
                viewModel.onSearchQueryChange(q)
            },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 0.dp)
        )
        val retryBrowse: () -> Unit = {
            if (searchQuery.isNotBlank()) {
                viewModel.onSearchQueryChange(searchQuery)
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
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
                ) {
                    items(
                        items = ui.songsList,
                        key = { it.id }
                    ) { song ->
                        TabletSongRow(
                            title = song.title,
                            artist = song.artist,
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

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 900, heightDp = 1200)
@Composable
private fun TabletHomeScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    CustomMusicAppTheme {
        TabletHomeScreen(
            viewModel = vm,
            onOpenPlayer = {},
            onLibraryMenuViewAlbum = {}
        )
    }
}
