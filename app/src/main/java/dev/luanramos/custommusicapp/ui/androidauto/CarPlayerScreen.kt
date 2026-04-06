package dev.luanramos.custommusicapp.ui.androidauto

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import dev.luanramos.custommusicapp.ui.components.AlbumArtPlaceholder
import dev.luanramos.custommusicapp.ui.components.CarPlayerTransportRow
import dev.luanramos.custommusicapp.ui.components.PlayerBufferingIndicator
import dev.luanramos.custommusicapp.ui.components.PlayerErrorMessage
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.components.PlayerSeekBar
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.theme.greyArtist
import dev.luanramos.custommusicapp.ui.util.canPlayAudio
import dev.luanramos.custommusicapp.ui.util.formatPlaybackElapsedSlashTotal
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.collectAsState

/**
 * Android Auto now-playing layout (Figma Player frame: header, art + meta, seek, wide transport).
 */
@Composable
fun CarPlayerScreen(
    viewModel: MusicViewModel,
    onBackToMusic: () -> Unit,
    onOpenAlbum: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by remember(viewModel) {
        viewModel.uiState.map { it.playbackState }
    }.collectAsStateWithLifecycle(initialValue = viewModel.uiState.collectAsState().value.playbackState)
    val track = state.currentTrack
    val canPlay = track.canPlayAudio()
    val durationMs = state.durationMs
    val positionMs = state.positionMs

    val repeatOn by viewModel.repeatOne.collectAsStateWithLifecycle()
    var showMenuSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(track?.id) {
        if (track == null) showMenuSheet = false
    }

    val blurModifier = remember {
        if (Build.VERSION.SDK_INT >= 31) Modifier.blur(88.dp) else Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AlbumArtPlaceholder(
            modifier = Modifier
                .align(Alignment.Center)
                .then(blurModifier)
                .alpha(0.45f),
            size = 560.dp,
            cornerDp = 28.dp
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .clickable(onClick = onBackToMusic)
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.car_destination_music),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, lineHeight = 34.sp),
                        color = Color.White
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .clickable(enabled = track != null, onClick = onOpenAlbum)
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Album,
                        contentDescription = stringResource(R.string.car_destination_album),
                        tint = if (track != null) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = stringResource(R.string.car_destination_album),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, lineHeight = 34.sp),
                        color = if (track != null) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AlbumArtPlaceholder(size = 240.dp, cornerDp = 16.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = track?.title ?: stringResource(R.string.player_no_track),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 44.sp, lineHeight = 50.sp),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track?.artist.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, lineHeight = 34.sp),
                        color = greyArtist,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (track != null && durationMs > 0) {
                        Text(
                            text = formatPlaybackElapsedSlashTotal(positionMs, durationMs),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                state.errorMessage?.let { PlayerErrorMessage(message = it) }

                if (track != null) {
                    PlayerSeekBar(
                        positionMs = positionMs,
                        durationMs = durationMs,
                        onSeekToFraction = { v ->
                            if (durationMs > 0) viewModel.seekTo((v * durationMs).toLong())
                        },
                        enabled = canPlay && durationMs > 0 && !state.isBuffering,
                        showTimeRow = false,
                        sliderHeight = 48.dp
                    )
                }

                PlayerBufferingIndicator(visible = state.isBuffering)

                if (track != null) {
                    CarPlayerTransportRow(
                        isPlaying = state.isPlaying,
                        canPlay = canPlay,
                        isBuffering = state.isBuffering,
                        repeatOn = repeatOn,
                        onPlayPause = {
                            if (state.isPlaying) viewModel.pause() else viewModel.resume()
                        },
                        onSkipPrevious = { viewModel.skipToPrevious() },
                        onSkipNext = { viewModel.skipToNext() },
                        onRepeatToggle = { viewModel.toggleRepeatOne() },
                        onViewAlbum = onOpenAlbum,
                        onMore = { showMenuSheet = true },
                        albumEnabled = true,
                        moreEnabled = true
                    )
                }
            }
        }

        if (showMenuSheet && track != null) {
            PlayerMenuBottomSheet(
                onDismiss = { showMenuSheet = false },
                songTitle = track.title,
                artistName = track.artist,
                onViewAlbumClick = {
                    showMenuSheet = false
                    onOpenAlbum()
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 720)
@Composable
private fun CarPlayerScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    LaunchedEffect(Unit) {
        FakeTrackPlaybackController.stop()
        vm.playTrack(
            Music(
                id = "preview",
                title = "Perfect",
                artist = "Ed Sheeran",
                songUrl = "https://example.com/p.mp3"
            )
        )
    }
    CustomMusicAppTheme {
        CarPlayerScreen(
            viewModel = vm,
            onBackToMusic = {},
            onOpenAlbum = {}
        )
    }
}
