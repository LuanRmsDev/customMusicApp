package dev.luanramos.custommusicapp.ui.smartphone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.ui.components.AlbumDisplayTopBar
import dev.luanramos.custommusicapp.ui.components.TrackAlbumArt
import dev.luanramos.custommusicapp.ui.components.AlbumDisplayTrackRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.util.isCompactPhoneLandscape

@Composable
fun AlbumDisplayScreen(
    albumTitle: String,
    artistName: String,
    tracks: List<Music>,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onTrackClick: (Music) -> Unit,
    modifier: Modifier = Modifier
) {
    val phoneLandscape = isCompactPhoneLandscape()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AlbumDisplayTopBar(
            title = albumTitle,
            onBack = onBack
        )
        if (isLoading && tracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (phoneLandscape) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                items(
                    items = tracks,
                    key = { it.id }
                ) { track ->
                    AlbumDisplayTrackRow(
                        title = track.title,
                        artist = track.artist,
                        onClick = { onTrackClick(track) },
                        track = track,
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrackAlbumArt(
                        track = tracks.firstOrNull(),
                        size = 120.dp,
                        cornerDp = 20.dp,
                        highRes = true,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = albumTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        lineHeight = 16.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
            ) {
                items(
                    items = tracks,
                    key = { it.id }
                ) { track ->
                    AlbumDisplayTrackRow(
                        title = track.title,
                        artist = track.artist,
                        onClick = { onTrackClick(track) },
                        track = track,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun AlbumDisplayScreenPreview() {
    CustomMusicAppTheme {
        AlbumDisplayScreen(
            albumTitle = LibraryMockedData.sampleDisplayAlbumTitle,
            artistName = LibraryMockedData.sampleDisplayAlbumArtist,
            tracks = LibraryMockedData.sampleDisplayAlbumTracks,
            onBack = {},
            onTrackClick = {}
        )
    }
}
