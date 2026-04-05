package dev.luanramos.custommusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.luanramos.custommusicapp.ui.util.DeviceFormFactor
import dev.luanramos.custommusicapp.ui.util.LocalDeviceAdaptationState
import dev.luanramos.custommusicapp.ui.util.primaryFormFactor

/**
 * Chooses the navigation graph from [LocalDeviceAdaptationState].
 * [DeviceFormFactor.Smartphone], [DeviceFormFactor.AndroidAuto], and [DeviceFormFactor.Tablet] use
 * dedicated hosts.
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    when (LocalDeviceAdaptationState.current.primaryFormFactor()) {
        DeviceFormFactor.Smartphone ->
            SmartphoneLibraryNavHost(modifier = modifier)

        DeviceFormFactor.AndroidAuto ->
            CarLibraryNavHost(modifier = modifier)

        DeviceFormFactor.Tablet ->
            TabletLibraryNavHost(modifier = modifier)

        DeviceFormFactor.Smartwatch ->
            WatchLibraryNavHost(modifier = modifier)
    }
}
