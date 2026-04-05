package dev.luanramos.custommusicapp.ui.watch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.no_internet_title),
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.no_internet_message),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth(),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Text(
                text = stringResource(R.string.action_retry),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 228, heightDp = 228)
@Composable
private fun NoInternetScreenPreview() {
    CustomMusicAppTheme {
        NoInternetScreen(onRetry = {})
    }
}
