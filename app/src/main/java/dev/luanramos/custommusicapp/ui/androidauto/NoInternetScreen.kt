package dev.luanramos.custommusicapp.ui.androidauto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
            .padding(horizontal = 36.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.no_internet_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 36.sp, lineHeight = 42.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.no_internet_message),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, lineHeight = 32.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Text(
                text = stringResource(R.string.action_retry),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 1280, heightDp = 720)
@Composable
private fun NoInternetScreenPreview() {
    CustomMusicAppTheme {
        NoInternetScreen(onRetry = {})
    }
}
