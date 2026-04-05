package dev.luanramos.custommusicapp.ui.androidauto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.ui.components.CarSongRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun CarAlbumListScreen(
    albumTitle: String,
    tracks: List<Music>,
    currentTrackId: String?,
    onBack: () -> Unit,
    onTrackClick: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = albumTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp, lineHeight = 36.sp),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = tracks, key = { it.id }) { track ->
                CarSongRow(
                    title = track.title,
                    artist = track.artist,
                    isPlaying = track.id == currentTrackId,
                    onClick = { onTrackClick(track) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 720)
@Composable
private fun CarAlbumListScreenPreview() {
    CustomMusicAppTheme {
        CarAlbumListScreen(
            albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
            tracks = LibraryMockedData.sampleDisplayAlbumTracks,
            currentTrackId = LibraryMockedData.sampleDisplayAlbumTracks.firstOrNull()?.id,
            onBack = {},
            onTrackClick = {}
        )
    }
}
