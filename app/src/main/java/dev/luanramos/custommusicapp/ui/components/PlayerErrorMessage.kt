package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun PlayerErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PlayerErrorMessagePreview() {
    CustomMusicAppTheme {
        PlayerErrorMessage(message = "Playback error")
    }
}
