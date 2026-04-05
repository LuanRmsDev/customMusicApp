package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.utils.formatPlaybackTimeMs

@Composable
fun PlayerSeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeekToFraction: (Float) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    timeLabelColor: Color = Color.White.copy(alpha = 0.6f)
) {
    val progress = if (durationMs > 0) {
        (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val remainingMs = (durationMs - positionMs).coerceAtLeast(0L)

    val white25 = Color.White.copy(alpha = 0.25f)
    val white60Track = Color.White.copy(alpha = 0.6f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Slider(
            value = progress,
            onValueChange = onSeekToFraction,
            enabled = enabled,
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
                color = timeLabelColor
            )
            Text(
                text = "-${formatPlaybackTimeMs(remainingMs)}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 14.sp,
                    lineHeight = 16.8.sp
                ),
                color = timeLabelColor
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlayerSeekBarPreview() {
    CustomMusicAppTheme {
        PlayerSeekBar(
            positionMs = 86_000L,
            durationMs = 220_000L,
            onSeekToFraction = {},
            enabled = true
        )
    }
}
