package dev.luanramos.custommusicapp.ui.tablet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.presentation.PreviewMusicRepository
import dev.luanramos.custommusicapp.ui.components.TrackAlbumArt
import dev.luanramos.custommusicapp.ui.components.PlayerBufferingIndicator
import dev.luanramos.custommusicapp.ui.components.PlayerErrorMessage
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.components.PlayerScreenTopBar
import dev.luanramos.custommusicapp.ui.components.SearchNoResults
import dev.luanramos.custommusicapp.ui.components.PlayerSeekBar
import dev.luanramos.custommusicapp.ui.components.TabletPlayerTransportRow
import dev.luanramos.custommusicapp.ui.components.TabletQueueSongRow
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.util.canPlayAudio

@Composable
fun TabletPlayerScreen(
    viewModel: MusicViewModel,
    onPlayerMenuViewAlbum: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val emptySearchQuery = ui.activeSearchQuery
    val state = ui.playbackState
    val track = state.currentTrack
    val canPlay = track.canPlayAudio()
    val durationMs = state.durationMs
    val positionMs = state.positionMs

    val repeatOn by viewModel.repeatOne.collectAsStateWithLifecycle()
    var showMenuSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(track?.id) {
        if (track == null) showMenuSheet = false
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 40.dp, vertical = 8.dp)
        ) {
            PlayerScreenTopBar(
                title = stringResource(R.string.player_now_playing_title),
                onBack = onBack,
                onMore = { if (track != null) showMenuSheet = true },
                moreEnabled = track != null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TrackAlbumArt(
                        track = track,
                        size = 286.dp,
                        cornerDp = 16.dp,
                        highRes = true,
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
                if (track == null) {
                    Text(
                        text = stringResource(R.string.player_no_track),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 32.sp,
                                lineHeight = 38.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                state.errorMessage?.let { PlayerErrorMessage(message = it) }

                if (track != null) {
                    PlayerSeekBar(
                        positionMs = positionMs,
                        durationMs = durationMs,
                        onSeekToFraction = { v ->
                            if (durationMs > 0) {
                                viewModel.seekTo((v * durationMs).toLong())
                            }
                        },
                        enabled = canPlay && durationMs > 0 && !state.isBuffering
                    )
                }

                PlayerBufferingIndicator(visible = state.isBuffering)

                if (track != null) {
                    TabletPlayerTransportRow(
                        isPlaying = state.isPlaying,
                        canPlay = canPlay,
                        isBuffering = state.isBuffering,
                        repeatOn = repeatOn,
                        onPlayPause = {
                            if (state.isPlaying) viewModel.pause() else viewModel.resume()
                        },
                        onSkipPrevious = { viewModel.skipToPrevious() },
                        onSkipNext = { viewModel.skipToNext() },
                        onRepeatToggle = { viewModel.toggleRepeatOne() }
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .width(288.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LibraryMusic,
                        contentDescription = stringResource(R.string.tablet_player_queue_icon_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                when {
                    ui.isLoading && ui.songsList.isEmpty() ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }

                    ui.songsList.isEmpty() && emptySearchQuery != null ->
                        SearchNoResults(
                            searchQuery = emptySearchQuery,
                            onRetry = { viewModel.submitSearchQuery(emptySearchQuery) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )

                    ui.songsList.isEmpty() ->
                        NoInternetScreen(
                            onRetry = { viewModel.retryLoadLibrary() },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )

                    else ->
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(
                                items = ui.songsList,
                                key = { it.id }
                            ) { song ->
                                TabletQueueSongRow(
                                    title = song.title,
                                    artist = song.artist,
                                    isCurrentTrack = song.id == state.currentTrack?.id,
                                    onClick = { viewModel.playTrack(song) },
                                    track = song,
                                )
                            }
                        }
                }
            }
        }
    }

    if (showMenuSheet && track != null) {
        PlayerMenuBottomSheet(
            onDismiss = { showMenuSheet = false },
            songTitle = track.title,
            artistName = track.artist,
            onViewAlbumClick = onPlayerMenuViewAlbum
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1194, heightDp = 834)
@Composable
private fun TabletPlayerScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    LaunchedEffect(Unit) {
        FakeTrackPlaybackController.stop()
        vm.playTrack(
            Music(
                id = "preview",
                title = "Get Lucky",
                artist = "Daft Punk feat. Pharrell Williams",
                songUrl = "https://example.com/preview.mp3"
            )
        )
    }
    CustomMusicAppTheme {
        TabletPlayerScreen(
            viewModel = vm,
            onPlayerMenuViewAlbum = {},
            onBack = {}
        )
    }
}
