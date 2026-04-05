package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

/** Tablet / iPad player controls: play 72dp, skips, trailing repeat (Figma Player frame). */
@Composable
fun TabletPlayerTransportRow(
    isPlaying: Boolean,
    canPlay: Boolean,
    isBuffering: Boolean,
    repeatOn: Boolean,
    onPlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onRepeatToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable(enabled = canPlay && !isBuffering) { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) {
                        stringResource(R.string.player_pause)
                    } else {
                        stringResource(R.string.player_play)
                    },
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
            IconButton(
                onClick = onSkipPrevious,
                enabled = canPlay,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.player_skip_previous),
                    modifier = Modifier.size(40.dp),
                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
                )
            }
            IconButton(
                onClick = onSkipNext,
                enabled = canPlay,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.player_skip_next),
                    modifier = Modifier.size(40.dp),
                    tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
                )
            }
        }
        IconButton(
            onClick = onRepeatToggle,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = stringResource(R.string.player_repeat),
                modifier = Modifier.size(28.dp),
                tint = if (repeatOn) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.White
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TabletPlayerTransportRowPreview() {
    CustomMusicAppTheme {
        TabletPlayerTransportRow(
            isPlaying = true,
            canPlay = true,
            isBuffering = false,
            repeatOn = false,
            onPlayPause = {},
            onSkipPrevious = {},
            onSkipNext = {},
            onRepeatToggle = {}
        )
    }
}
