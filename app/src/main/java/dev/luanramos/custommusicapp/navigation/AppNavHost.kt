package dev.luanramos.custommusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.luanramos.custommusicapp.ui.util.DeviceFormFactor
import dev.luanramos.custommusicapp.ui.util.LocalDeviceAdaptationState
import dev.luanramos.custommusicapp.ui.util.primaryFormFactor

/**
 * Chooses the navigation graph from [LocalDeviceAdaptationState].
 * Only [DeviceFormFactor.Smartphone] uses the full library graph today; other factors show
 * [PendingFormFactorRoot] until their UIs are implemented.
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    when (val formFactor = LocalDeviceAdaptationState.current.primaryFormFactor()) {
        DeviceFormFactor.Smartphone ->
            SmartphoneLibraryNavHost(modifier = modifier)

        DeviceFormFactor.Tablet,
        DeviceFormFactor.AndroidAuto,
        DeviceFormFactor.Smartwatch ->
            PendingFormFactorRoot(formFactor = formFactor, modifier = modifier)
    }
}
