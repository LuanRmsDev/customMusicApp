package dev.luanramos.custommusicapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private val SplashBlack = Color(0xFF000000)
private val SplashTeal = Color(0xFF0086A0)
private const val CssGradientAngleDeg = 39.0
private const val CssBlackStopPercent = 33.57f
private const val CssTealStopPercent = 205.11f
private val CssBlackStopOnLine = CssBlackStopPercent / 100f

@Composable
fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val angleRad = CssGradientAngleDeg * PI / 180.0
                val dx = sin(angleRad).toFloat()
                val dy = -cos(angleRad).toFloat()
                val cx = size.width * 0.5f
                val cy = size.height * 0.5f
                val halfLen = hypot(size.width.toDouble(), size.height.toDouble()).toFloat() * 0.5f
                val start = Offset(cx - dx * halfLen, cy - dy * halfLen)
                val end = Offset(cx + dx * halfLen, cy + dy * halfLen)
                val endBlend =
                    ((100f - CssBlackStopPercent) / (CssTealStopPercent - CssBlackStopPercent))
                        .coerceIn(0f, 1f)
                val endColor = Color(
                    red = SplashBlack.red + (SplashTeal.red - SplashBlack.red) * endBlend,
                    green = SplashBlack.green + (SplashTeal.green - SplashBlack.green) * endBlend,
                    blue = SplashBlack.blue + (SplashTeal.blue - SplashBlack.blue) * endBlend
                )
                drawRect(
                    brush = Brush.linearGradient(
                        0f to SplashBlack,
                        CssBlackStopOnLine to SplashBlack,
                        1f to endColor,
                        start = start,
                        end = end
                    ),
                    size = size
                )
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_music_note),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashContentPreview() {
    CustomMusicAppTheme(darkTheme = true, dynamicColor = false) {
        SplashContent()
    }
}
