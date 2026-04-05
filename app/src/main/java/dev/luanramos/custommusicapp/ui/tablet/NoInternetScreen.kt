package dev.luanramos.custommusicapp.ui.tablet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun NoInternetScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.no_internet_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, lineHeight = 34.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.no_internet_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .widthIn(max = 420.dp),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 28.dp),
        ) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 900, heightDp = 600)
@Composable
private fun NoInternetScreenPreview() {
    CustomMusicAppTheme {
        NoInternetScreen(onRetry = {})
    }
}
