package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun PlayerAlbumArtSection(
    track: Music?,
    modifier: Modifier = Modifier,
    artSize: Dp = 264.dp,
    cornerDp: Dp = 20.dp,
    topSpacing: Dp = 24.dp,
    bottomSpacing: Dp = 24.dp,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacing))
        TrackAlbumArt(
            track = track,
            size = artSize,
            cornerDp = cornerDp,
            highRes = true,
        )
        Spacer(modifier = Modifier.height(bottomSpacing))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlayerAlbumArtSectionPreview() {
    CustomMusicAppTheme {
        PlayerAlbumArtSection(track = null)
    }
}
