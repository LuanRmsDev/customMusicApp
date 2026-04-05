package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun PlayerTrackHeader(
    title: String?,
    artist: String?,
    emptyLabel: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    artistColor: Color = Color.White.copy(alpha = 0.7f),
    emptyColor: Color = Color.White.copy(alpha = 0.6f)
) {
    if (title.isNullOrBlank()) {
        Text(
            text = emptyLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = emptyColor,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth()
        )
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = titleColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = artist.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = artistColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlayerTrackHeaderPreview() {
    CustomMusicAppTheme {
        PlayerTrackHeader(
            title = "Get Lucky",
            artist = "Daft Punk feat. Pharrell Williams",
            emptyLabel = ""
        )
    }
}
