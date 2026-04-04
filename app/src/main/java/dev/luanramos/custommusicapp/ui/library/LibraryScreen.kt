package dev.luanramos.custommusicapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.ui.components.LibrarySongRow
import dev.luanramos.custommusicapp.ui.components.LibraryTopBar
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun LibraryScreen(
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LibraryTopBar(
            title = stringResource(R.string.songs_screen_title),
            modifier = Modifier.statusBarsPadding(),
            onSearchClick = { /* search — wire when flow exists */ }
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        ) {
            itemsIndexed(
                items = LibraryMockedData.songs,
                key = { index, song -> "${index}_${song.title}_${song.artist}" }
            ) { _, song ->
                LibrarySongRow(
                    title = song.title,
                    artist = song.artist,
                    onRowClick = onOpenPlayer,
                    onMoreClick = { /* overflow menu */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun LibraryScreenPreview() {
    CustomMusicAppTheme {
        LibraryScreen(onOpenPlayer = {})
    }
}
