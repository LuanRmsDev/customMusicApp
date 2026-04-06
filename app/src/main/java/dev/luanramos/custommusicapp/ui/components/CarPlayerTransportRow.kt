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
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
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

/**
 * Android Auto / wide player control strip: repeat | previous | play | next | album | more
 */
@Composable
fun CarPlayerTransportRow(
    isPlaying: Boolean,
    canPlay: Boolean,
    isBuffering: Boolean,
    repeatOn: Boolean,
    onPlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onRepeatToggle: () -> Unit,
    onViewAlbum: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
    albumEnabled: Boolean = true,
    moreEnabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onRepeatToggle,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = stringResource(R.string.player_repeat),
                modifier = Modifier.size(40.dp),
                tint = if (repeatOn) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.White
                }
            )
        }
        IconButton(
            onClick = onSkipPrevious,
            enabled = canPlay,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = stringResource(R.string.player_skip_previous),
                modifier = Modifier.size(48.dp),
                tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
            )
        }
        Box(
            modifier = Modifier
                .size(96.dp)
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
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
        IconButton(
            onClick = onSkipNext,
            enabled = canPlay,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = stringResource(R.string.player_skip_next),
                modifier = Modifier.size(48.dp),
                tint = if (canPlay) Color.White else Color.White.copy(alpha = 0.38f)
            )
        }
        IconButton(
            onClick = onViewAlbum,
            enabled = albumEnabled,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Album,
                contentDescription = stringResource(R.string.car_destination_album),
                modifier = Modifier.size(40.dp),
                tint = if (albumEnabled) Color.White else Color.White.copy(alpha = 0.38f)
            )
        }
        IconButton(
            onClick = onMore,
            enabled = moreEnabled,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.player_open_menu),
                modifier = Modifier.size(40.dp),
                tint = if (moreEnabled) Color.White else Color.White.copy(alpha = 0.38f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CarPlayerTransportRowPreview() {
    CustomMusicAppTheme {
        CarPlayerTransportRow(
            isPlaying = true,
            canPlay = true,
            isBuffering = false,
            repeatOn = false,
            onPlayPause = {},
            onSkipPrevious = {},
            onSkipNext = {},
            onRepeatToggle = {},
            onViewAlbum = {},
            onMore = {}
        )
    }
}
