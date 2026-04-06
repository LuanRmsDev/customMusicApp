package dev.luanramos.custommusicapp.ui.androidauto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.ui.components.CarSongRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun CarBrowseScreen(
    songs: List<Music>,
    currentTrackId: String?,
    isCatalogLoading: Boolean,
    onRetryCatalog: () -> Unit,
    onSongClick: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 36.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.car_browse_title),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp, lineHeight = 40.sp),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        when {
            isCatalogLoading && songs.isEmpty() ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

            songs.isEmpty() ->
                NoInternetScreen(
                    onRetry = onRetryCatalog,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )

            else ->
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = songs, key = { it.id }) { song ->
                        CarSongRow(
                            title = song.title,
                            artist = song.artist,
                            isPlaying = song.id == currentTrackId,
                            onClick = { onSongClick(song) },
                            track = song,
                        )
                    }
                }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 720)
@Composable
private fun CarBrowseScreenPreview() {
    CustomMusicAppTheme {
        CarBrowseScreen(
            songs = LibraryMockedData.songs,
            currentTrackId = LibraryMockedData.songs.firstOrNull()?.id,
            isCatalogLoading = false,
            onRetryCatalog = {},
            onSongClick = {}
        )
    }
}
