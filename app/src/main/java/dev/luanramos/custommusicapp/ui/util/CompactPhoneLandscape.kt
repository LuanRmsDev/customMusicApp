package dev.luanramos.custommusicapp.ui.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Phone-sized window in landscape (not tablet / expanded width). */
@Composable
fun isCompactPhoneLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val compactWidth = configuration.smallestScreenWidthDp < 600
    return landscape && compactWidth
}
