package dev.luanramos.custommusicapp.ui.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import dev.luanramos.custommusicapp.ui.components.AlbumArtPlaceholder
import dev.luanramos.custommusicapp.ui.components.PlayerBufferingIndicator
import dev.luanramos.custommusicapp.ui.components.PlayerErrorMessage
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.util.canPlayAudio
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

/**
 * Wear now playing: full-bleed circular art, metadata and time at top, controls overlaid on art,
 * circular progress around play, repeat below (Figma `11020-2554` + companion nav spec).
 *
 * **Swipe down** returns to the Music hub (same as Figma “Player → Nav” note).
 * **View album:** tap the circular artwork when a track is loaded.
 */
@Composable
fun WatchPlayerScreen(
    viewModel: MusicViewModel,
    onPlayerMenuViewAlbum: () -> Unit,
    onSwipeDownToMainNav: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by remember(viewModel) {
        viewModel.uiState.map { it.playbackState }
    }.collectAsStateWithLifecycle(initialValue = viewModel.uiState.value.playbackState)
    val track = state.currentTrack
    val canPlay = track.canPlayAudio()
    val durationMs = state.durationMs
    val positionMs = state.positionMs

    var repeatOn by rememberSaveable { mutableStateOf(false) }

    var timeText by remember { mutableStateOf(formatWatchTime()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(30_000L)
            timeText = formatWatchTime()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(onSwipeDownToMainNav) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onVerticalDrag = { _, dragAmount -> totalDrag += dragAmount },
                    onDragEnd = {
                        if (totalDrag > 72f) {
                            onSwipeDownToMainNav()
                        }
                        totalDrag = 0f
                    },
                    onDragCancel = { totalDrag = 0f }
                )
            }
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val artSize = minOf(maxWidth, maxHeight) * 0.88f
            val corner = artSize / 2
            val artModifier = if (track != null) {
                Modifier
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = stringResource(R.string.player_action_view_album),
                        onClick = onPlayerMenuViewAlbum
                    )
            } else {
                Modifier.clip(CircleShape)
            }
            Box(
                modifier = Modifier
                    .size(artSize)
                    .align(Alignment.Center)
                    .then(artModifier)
            ) {
                AlbumArtPlaceholder(size = artSize, cornerDp = corner)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.72f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 8.dp, start = 18.dp, end = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = Color.White.copy(alpha = 0.95f)
            )
            if (track == null) {
                Text(
                    text = stringResource(R.string.player_no_track),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            } else {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.errorMessage?.let { msg ->
            PlayerErrorMessage(
                message = msg,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp, start = 12.dp, end = 12.dp)
            )
        }

        PlayerBufferingIndicator(
            visible = state.isBuffering,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 40.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.skipToPrevious() },
                enabled = canPlay,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.player_skip_previous),
                    modifier = Modifier.size(26.dp),
                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.35f)
                )
            }

            Box(
                modifier = Modifier.size(78.dp),
                contentAlignment = Alignment.Center
            ) {
                if (track != null && durationMs > 0) {
                    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.22f),
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                        .clickable(enabled = canPlay && !state.isBuffering) {
                            if (state.isPlaying) viewModel.pause() else viewModel.resume()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) {
                            stringResource(R.string.player_pause)
                        } else {
                            stringResource(R.string.player_play)
                        },
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }

            IconButton(
                onClick = { viewModel.skipToNext() },
                enabled = canPlay,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.player_skip_next),
                    modifier = Modifier.size(26.dp),
                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.35f)
                )
            }
        }

        IconButton(
            onClick = { repeatOn = !repeatOn },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = stringResource(R.string.player_repeat),
                modifier = Modifier.size(24.dp),
                tint = if (repeatOn) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.White
                }
            )
        }
    }
}

private fun formatWatchTime(): String =
    LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm"))

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 228, heightDp = 228)
@Composable
private fun WatchPlayerScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    LaunchedEffect(Unit) {
        FakeTrackPlaybackController.stop()
        vm.playTrack(
            Music(
                id = "preview",
                title = "Perfect",
                artist = "Ed Sheeran",
                songUrl = "https://example.com/preview.mp3"
            )
        )
    }
    CustomMusicAppTheme {
        WatchPlayerScreen(
            viewModel = vm,
            onPlayerMenuViewAlbum = {},
            onSwipeDownToMainNav = {}
        )
    }
}
