package dev.luanramos.custommusicapp.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker

/**
 * Describes where the UI is running so screens can branch (phone vs tablet vs car vs watch).
 *
 * * **Smartwatch** — [PackageManager.FEATURE_WATCH].
 * * **Tablet-sized window** — [Configuration.smallestScreenWidthDp] ≥ 600 (tablets, unfolded foldables,
 *   large split windows). Not mutually exclusive with foldable signals.
 * * **Foldable** — hinge sensor and/or a [FoldingFeature] intersecting the current window.
 * * **Android Auto** — [CarConnection] projection from the phone to the car UI.
 * * **Embedded automotive** — app running as an Android Automotive / embedded car app host.
 */
data class DeviceAdaptationState(
    val isSmartwatch: Boolean,
    val isTabletSizedWindow: Boolean,
    val isFoldableWindowLayout: Boolean,
    val hasFoldableHardware: Boolean,
    val androidAutoConnection: AndroidAutoConnection,
) {
    enum class AndroidAutoConnection {
        None,
        /** Phone projecting to Android Auto (driver display). */
        PhoneProjection,
        /** Android Automotive OS / embedded car experience. */
        EmbeddedAutomotive,
    }

    val isAndroidAutoProjection: Boolean
        get() = androidAutoConnection == AndroidAutoConnection.PhoneProjection

    val isAndroidAutomotiveEmbedded: Boolean
        get() = androidAutoConnection == AndroidAutoConnection.EmbeddedAutomotive

    val isAnyCarExperience: Boolean
        get() = androidAutoConnection != AndroidAutoConnection.None

    /**
     * Typical handset bucket: not watch, not tablet-width window, not car session from [androidAutoConnection].
     */
    val isSmartphoneHandset: Boolean
        get() = !isSmartwatch && !isTabletSizedWindow && androidAutoConnection == AndroidAutoConnection.None

    /**
     * Prefer tablet-style chrome (side rails, grid density): wide window on a non-watch device.
     */
    val isTabletExperience: Boolean
        get() = !isSmartwatch && isTabletSizedWindow

    /**
     * Foldable hardware or current window is fold-aware (hinge in layout).
     */
    val isFoldableExperience: Boolean
        get() = hasFoldableHardware || isFoldableWindowLayout
}

/** Primary UI bucket for root navigation (first match wins). */
enum class DeviceFormFactor {
    Smartwatch,
    AndroidAuto,
    Tablet,
    Smartphone,
}

fun DeviceAdaptationState.primaryFormFactor(): DeviceFormFactor =
    when {
        isSmartwatch -> DeviceFormFactor.Smartwatch
        isAnyCarExperience -> DeviceFormFactor.AndroidAuto
        isTabletExperience -> DeviceFormFactor.Tablet
        else -> DeviceFormFactor.Smartphone
    }

fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/** Wear OS / watch form factor. */
fun Context.isWearOsDevice(): Boolean =
    packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)

/**
 * Smallest width ≥ 600dp — Material “tablet” breakpoint for this window
 * (includes many unfolded foldables and split-screen on large displays).
 */
fun Configuration.isTabletSizedWindow(): Boolean = smallestScreenWidthDp >= 600

/** Hinge angle sensor (API 30+); indicates foldable-style hardware. */
fun Context.reportsFoldableHingeSensor(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
        packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_HINGE_ANGLE)

/**
 * Non-Compose snapshot using current configuration only (no fold posture / no Android Auto).
 * For full signals use [rememberDeviceAdaptationState].
 */
fun Context.deviceAdaptationStatic(configuration: Configuration): DeviceAdaptationState =
    DeviceAdaptationState(
        isSmartwatch = isWearOsDevice(),
        isTabletSizedWindow = configuration.isTabletSizedWindow(),
        isFoldableWindowLayout = false,
        hasFoldableHardware = reportsFoldableHingeSensor(),
        androidAutoConnection = DeviceAdaptationState.AndroidAutoConnection.None,
    )

// Older androidx.car:app versions omit CONNECTION_TYPE_EMBEDDED; values match Car App API.
private const val CarConnectionTypeEmbedded = 2

private fun connectionTypeToMode(type: Int): DeviceAdaptationState.AndroidAutoConnection =
    when (type) {
        CarConnection.CONNECTION_TYPE_PROJECTION ->
            DeviceAdaptationState.AndroidAutoConnection.PhoneProjection
        CarConnectionTypeEmbedded ->
            DeviceAdaptationState.AndroidAutoConnection.EmbeddedAutomotive
        else -> DeviceAdaptationState.AndroidAutoConnection.None
    }

/**
 * Collects fold posture and Android Auto connection updates for adaptive layouts.
 * Call from a Composable that stays in the tree while the activity is shown (e.g. below [setContent]).
 */
@Composable
fun rememberDeviceAdaptationState(): DeviceAdaptationState {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(context) { context.findActivity() }

    val isSmartwatch = remember(context) { context.isWearOsDevice() }
    val isTabletSized = remember(configuration) { configuration.isTabletSizedWindow() }
    val hasFoldableHw = remember(context) { context.reportsFoldableHingeSensor() }

    var foldInWindow by remember { mutableStateOf(false) }
    LaunchedEffect(activity, lifecycleOwner) {
        val act = activity ?: run {
            foldInWindow = false
            return@LaunchedEffect
        }
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            WindowInfoTracker.getOrCreate(act)
                .windowLayoutInfo(act)
                .collect { info ->
                    foldInWindow = info.displayFeatures.any { it is FoldingFeature }
                }
        }
    }

    var connectionType by remember { mutableIntStateOf(CarConnection.CONNECTION_TYPE_NOT_CONNECTED) }
    val carConnection = remember(context.applicationContext) { CarConnection(context.applicationContext) }
    DisposableEffect(carConnection, lifecycleOwner) {
        val observer = Observer<Int> { connectionType = it }
        carConnection.type.observeForever(observer)
        onDispose { carConnection.type.removeObserver(observer) }
    }

    val carMode = connectionTypeToMode(connectionType)

    return DeviceAdaptationState(
        isSmartwatch = isSmartwatch,
        isTabletSizedWindow = isTabletSized,
        isFoldableWindowLayout = foldInWindow,
        hasFoldableHardware = hasFoldableHw,
        androidAutoConnection = carMode,
    )
}

/**
 * App-wide [DeviceAdaptationState], provided once from the activity `setContent` root.
 * Use [CompositionLocalProvider] + [rememberDeviceAdaptationState], or call [rememberDeviceAdaptationState]
 * directly in a single root composable.
 */
val LocalDeviceAdaptationState = staticCompositionLocalOf<DeviceAdaptationState> {
    error("LocalDeviceAdaptationState not provided — use ProvideDeviceAdaptationState at the root.")
}

@Composable
fun ProvideDeviceAdaptationState(content: @Composable () -> Unit) {
    val state = rememberDeviceAdaptationState()
    CompositionLocalProvider(LocalDeviceAdaptationState provides state, content = content)
}
