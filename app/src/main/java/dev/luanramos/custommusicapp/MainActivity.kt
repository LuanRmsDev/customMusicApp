package dev.luanramos.custommusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.luanramos.custommusicapp.navigation.LibraryNavHost
import dev.luanramos.custommusicapp.ui.splash.SplashContent
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import kotlinx.coroutines.delay

private const val SplashVisibleMillis = 2_000L

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomMusicAppTheme {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(SplashVisibleMillis)
                    showSplash = false
                }
                Crossfade(
                    targetState = showSplash,
                    modifier = Modifier.fillMaxSize(),
                    label = "splash"
                ) { splash ->
                    if (splash) {
                        SplashContent(modifier = Modifier.fillMaxSize())
                    } else {
                        LibraryNavHost(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
