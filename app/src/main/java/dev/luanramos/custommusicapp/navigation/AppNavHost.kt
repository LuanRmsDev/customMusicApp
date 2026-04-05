package dev.luanramos.custommusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.luanramos.custommusicapp.ui.util.DeviceFormFactor
import dev.luanramos.custommusicapp.ui.util.LocalDeviceAdaptationState
import dev.luanramos.custommusicapp.ui.util.primaryFormFactor

/**
 * Chooses the navigation graph from [LocalDeviceAdaptationState].
 * [DeviceFormFactor.Smartphone] and [DeviceFormFactor.AndroidAuto] use dedicated hosts; tablet and
 * watch still use [PendingFormFactorRoot] until implemented.
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    when (val formFactor = LocalDeviceAdaptationState.current.primaryFormFactor()) {
        DeviceFormFactor.Smartphone ->
            SmartphoneLibraryNavHost(modifier = modifier)

        DeviceFormFactor.AndroidAuto ->
            CarLibraryNavHost(modifier = modifier)

        DeviceFormFactor.Tablet,
        DeviceFormFactor.Smartwatch ->
            PendingFormFactorRoot(formFactor = formFactor, modifier = modifier)
    }
}
