package dev.luanramos.custommusicapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = brightTeal,
    onPrimary = nearBlack,
    primaryContainer = deepTeal,
    onPrimaryContainer = icyBlue,
    secondary = teal,
    onSecondary = white,
    secondaryContainer = deepTealDark,
    onSecondaryContainer = icyBlue,
    tertiary = slate,
    onTertiary = deepGreenBlack,
    background = black,
    onBackground = white,
    surface = darkGrey,
    onSurface = white,
    surfaceVariant = mediumGrey,
    onSurfaceVariant = greyArtist,
    outline = borderGrey,
    outlineVariant = greyIcon,
    error = lightPink,
    onError = darkRed,
    errorContainer = redBrown,
    onErrorContainer = palePink
)

@Composable
fun CustomMusicAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
