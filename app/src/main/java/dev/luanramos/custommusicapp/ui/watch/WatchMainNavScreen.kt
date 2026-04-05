package dev.luanramos.custommusicapp.ui.watch

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private val WatchNavRowShape = RoundedCornerShape(22.dp)
private val WatchNavIconTileShape = RoundedCornerShape(8.dp)
private val WatchNavRowBackground = Color(0xFF2C2C32)
private val WatchNavIconTileBackground = Color(0xFF1A1A1F)

/**
 * Wear “Music” hub: time, title, and menu rows (Figma main navigation next to player).
 */
@Composable
fun WatchMainNavScreen(
    onNowPlayingClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeText by remember { mutableStateOf(formatWatchTime()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(30_000L)
            timeText = formatWatchTime()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = Color.White.copy(alpha = 0.9f)
        )
        Text(
            text = stringResource(R.string.watch_nav_title_music),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        WatchNavMenuRow(
            icon = Icons.Filled.PlayArrow,
            label = stringResource(R.string.watch_nav_now_playing),
            onClick = onNowPlayingClick
        )
        WatchNavMenuRow(
            icon = Icons.Filled.Album,
            label = stringResource(R.string.watch_nav_albums),
            onClick = onAlbumsClick
        )
        WatchNavMenuRow(
            icon = Icons.Filled.MusicNote,
            label = stringResource(R.string.watch_nav_songs),
            onClick = onSongsClick
        )
    }
}

@Composable
private fun WatchNavMenuRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(WatchNavRowShape)
            .background(WatchNavRowBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(WatchNavIconTileShape)
                .background(WatchNavIconTileBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White
        )
    }
}

private fun formatWatchTime(): String =
    LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm"))

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 228, heightDp = 228)
@Composable
private fun WatchMainNavScreenPreview() {
    CustomMusicAppTheme {
        WatchMainNavScreen(
            onNowPlayingClick = {},
            onAlbumsClick = {},
            onSongsClick = {}
        )
    }
}
