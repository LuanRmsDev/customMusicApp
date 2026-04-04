package dev.luanramos.custommusicapp.ui.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.ui.components.LibrarySearchField
import dev.luanramos.custommusicapp.ui.components.LibrarySongRow
import dev.luanramos.custommusicapp.ui.components.LibraryTopBar
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun LibraryScreen(
    playback: TrackPlaybackController,
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    val songs = remember(searchQuery) {
        filterSongs(LibraryMockedData.songs, searchQuery)
    }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            searchFocusRequester.requestFocus()
        } else {
            keyboard?.hide()
            searchQuery = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LibraryTopBar(
            title = stringResource(R.string.songs_screen_title),
            modifier = Modifier.statusBarsPadding(),
            isSearchActive = isSearchActive,
            onSearchToggleClick = {
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
                focusRequester = searchFocusRequester,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        ) {
            items(
                items = songs,
                key = { it.id }
            ) { song ->
                LibrarySongRow(
                    title = song.title,
                    artist = song.artist,
                    onRowClick = {
                        playback.play(song)
                        onOpenPlayer()
                    },
                    onMoreClick = { /* overflow menu */ }
                )
            }
        }
    }
}

private fun filterSongs(songs: List<Music>, query: String): List<Music> {
    val q = query.trim()
    if (q.isEmpty()) return songs
    return songs.filter { song ->
        song.title.contains(q, ignoreCase = true) ||
            song.artist.contains(q, ignoreCase = true)
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun LibraryScreenPreview() {
    CustomMusicAppTheme {
        LibraryScreen(
            playback = FakeTrackPlaybackController,
            onOpenPlayer = {}
        )
    }
}
