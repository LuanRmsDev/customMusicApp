package dev.luanramos.custommusicapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme

@Composable
fun SplashContent(modifier: Modifier = Modifier) {

    val brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to Color(0xFF0086A0),
            0f to Color(0xFF004F5C),
            0.8f to Color(0xFF000000),
            1.0f to Color(0xFF000000)
        ),
        start = Offset(Float.POSITIVE_INFINITY, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
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
