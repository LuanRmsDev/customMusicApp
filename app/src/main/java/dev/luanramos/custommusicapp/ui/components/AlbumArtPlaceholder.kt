package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun AlbumArtPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    cornerDp: Dp = 8.dp,
    /** When true, [modifier] should fill the parent; [size] is ignored for layout. */
    fillMax: Boolean = false,
) {
    val shape = RoundedCornerShape(cornerDp)
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        )
    )
    Box(
        modifier = modifier
            .then(if (fillMax) Modifier else Modifier.size(size))
            .clip(shape)
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxSize(0.45f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AlbumArtPlaceholderPreview() {
    CustomMusicAppTheme {
        AlbumArtPlaceholder()
    }
}
