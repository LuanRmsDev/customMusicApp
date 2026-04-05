package dev.luanramos.custommusicapp.ui.tablet

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.ui.components.LibrarySearchField
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.components.TabletSongRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun TabletHomeScreen(
    playback: TrackPlaybackController,
    onOpenPlayer: () -> Unit,
    onLibraryMenuViewAlbum: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var menuSongId by rememberSaveable { mutableStateOf<String?>(null) }

    val songsById = remember {
        (LibraryMockedData.songs + LibraryMockedData.sampleDisplayAlbumTracks)
            .associateBy { it.id }
    }
    val menuSong = remember(menuSongId) { menuSongId?.let { songsById[it] } }

    val songs = remember(searchQuery) {
        filterSongs(LibraryMockedData.songs, searchQuery)
    }

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
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 0.dp)
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
        ) {
            items(
                items = songs,
                key = { it.id }
            ) { song ->
                TabletSongRow(
                    title = song.title,
                    artist = song.artist,
                    onRowClick = {
                        playback.play(song)
                        onOpenPlayer()
                    },
                    onMoreClick = { menuSongId = song.id }
                )
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

private fun filterSongs(songs: List<Music>, query: String): List<Music> {
    val q = query.trim()
    if (q.isEmpty()) return songs
    return songs.filter { song ->
        song.title.contains(q, ignoreCase = true) ||
            song.artist.contains(q, ignoreCase = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 900, heightDp = 1200)
@Composable
private fun TabletHomeScreenPreview() {
    CustomMusicAppTheme {
        TabletHomeScreen(
            playback = FakeTrackPlaybackController,
            onOpenPlayer = {},
            onLibraryMenuViewAlbum = {}
        )
    }
}
