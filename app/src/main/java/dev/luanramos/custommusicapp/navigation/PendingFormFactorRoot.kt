package dev.luanramos.custommusicapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.util.DeviceFormFactor

/**
 * Root shown for form factors that do not have a dedicated UI graph yet.
 * Replace with real nav hosts when tablet / car / watch screens are implemented.
 */
@Composable
fun PendingFormFactorRoot(
    formFactor: DeviceFormFactor,
    modifier: Modifier = Modifier
) {
    val label = stringResource(
        when (formFactor) {
            DeviceFormFactor.Tablet -> R.string.device_form_factor_tablet
            DeviceFormFactor.AndroidAuto -> R.string.device_form_factor_android_auto
            DeviceFormFactor.Smartwatch -> R.string.device_form_factor_smartwatch
            DeviceFormFactor.Smartphone -> R.string.device_form_factor_smartphone
        }
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.device_ui_pending_message, label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
