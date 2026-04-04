package dev.luanramos.custommusicapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.ui.components.AlbumArtPlaceholder
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.utils.formatPlaybackTimeMs

@Composable
fun LibraryPlayerScreen(
    playback: TrackPlaybackController,
    onOpenAlbumDetails: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by playback.state.collectAsStateWithLifecycle()
    val track = state.currentTrack
    val canPlay = track != null && track.songUrl != null
    val durationMs = state.durationMs
    val positionMs = state.positionMs
    val progress = if (durationMs > 0) {
        (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val remainingMs = (durationMs - positionMs).coerceAtLeast(0L)

    var repeatOn by remember { mutableStateOf(false) }

    val onSurface = MaterialTheme.colorScheme.onBackground
    val white70 = Color.White.copy(alpha = 0.7f)
    val white60 = Color.White.copy(alpha = 0.6f)
    val white25 = Color.White.copy(alpha = 0.25f)
    val white60Track = Color.White.copy(alpha = 0.6f)
    val playCircleBg = Color.White.copy(alpha = 0.2f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTopBar(
            onBack = onBack,
            onMore = onOpenAlbumDetails
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                AlbumArtPlaceholder(
                    size = 264.dp,
                    cornerDp = 20.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (track == null) {
                    Text(
                        text = stringResource(R.string.player_no_track),
                        style = MaterialTheme.typography.bodyLarge,
                        color = white60,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = white70,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (track != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Slider(
                            value = progress,
                            onValueChange = { v ->
                                if (durationMs > 0) {
                                    playback.seekTo((v * durationMs).toLong())
                                }
                            },
                            enabled = canPlay && durationMs > 0 && !state.isBuffering,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = white60Track,
                                inactiveTrackColor = white25,
                                disabledThumbColor = Color.White.copy(alpha = 0.4f),
                                disabledActiveTrackColor = white60Track.copy(alpha = 0.4f),
                                disabledInactiveTrackColor = white25.copy(alpha = 0.4f)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatPlaybackTimeMs(positionMs),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 16.8.sp
                                ),
                                color = white60
                            )
                            Text(
                                text = "-${formatPlaybackTimeMs(remainingMs)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 16.8.sp
                                ),
                                color = white60
                            )
                        }
                    }
                }

                if (state.isBuffering) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp,
                            color = onSurface
                        )
                    }
                }

                if (track != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(playCircleBg)
                                    .clickable(
                                        enabled = canPlay && !state.isBuffering
                                    ) {
                                        if (state.isPlaying) playback.pause() else playback.resume()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (state.isPlaying) {
                                        Icons.Filled.Pause
                                    } else {
                                        Icons.Filled.PlayArrow
                                    },
                                    contentDescription = if (state.isPlaying) {
                                        stringResource(R.string.player_pause)
                                    } else {
                                        stringResource(R.string.player_play)
                                    },
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = { playback.skipToPrevious() },
                                enabled = canPlay,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = stringResource(R.string.player_skip_previous),
                                    modifier = Modifier.size(36.dp),
                                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
                                )
                            }
                            IconButton(
                                onClick = { playback.skipToNext() },
                                enabled = canPlay,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = stringResource(R.string.player_skip_next),
                                    modifier = Modifier.size(36.dp),
                                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
                                )
                            }
                        }
                        IconButton(
                            onClick = { repeatOn = !repeatOn },
                            modifier = Modifier.size(48.dp)
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
            }
        }
    }
}

@Composable
private fun PlayerTopBar(
    onBack: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = stringResource(R.string.player_now_playing_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 18.sp,
                lineHeight = 19.44.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = onMore,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.player_open_menu),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun LibraryPlayerScreenPreview() {
    LaunchedEffect(Unit) {
        FakeTrackPlaybackController.stop()
        FakeTrackPlaybackController.play(
            Music(
                id = "preview",
                title = "Get Lucky",
                artist = "Daft Punk feat. Pharrell Williams",
                songUrl = "https://example.com/preview.mp3"
            )
        )
    }
    CustomMusicAppTheme {
        LibraryPlayerScreen(
            playback = FakeTrackPlaybackController,
            onOpenAlbumDetails = {},
            onBack = {}
        )
    }
}
