package dev.luanramos.custommusicapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.ui.util.resolveAlbumArtLoadData

/**
 * Loads album art via Coil: network URL sized for display, or [Music.localArtworkPath] when offline / as fallback.
 */
@Composable
fun TrackAlbumArt(
    track: Music?,
    modifier: Modifier = Modifier,
    size: Dp,
    cornerDp: Dp = 8.dp,
    clipShape: Shape? = null,
    contentScale: ContentScale = ContentScale.Crop,
    /** When true, decodes a larger bitmap (hero / blur backgrounds). */
    highRes: Boolean = false,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val displayPx = remember(size, density) {
        with(density) { size.roundToPx() }.coerceAtLeast(1)
    }
    val decodePx = remember(displayPx, highRes) {
        val factor = if (highRes) 2.5 else 1.5
        (displayPx * factor).toInt().coerceIn(32, if (highRes) 1200 else 800)
    }
    val data = remember(track?.id, track?.localArtworkPath, track?.artwork, decodePx) {
        resolveAlbumArtLoadData(track, decodePx, context)
    }
    val shape = clipShape ?: RoundedCornerShape(cornerDp)
    val cd = stringResource(R.string.album_art_content_description)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
    ) {
        if (data != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data)
                    .size(Size(decodePx, decodePx))
                    .crossfade(true)
                    .build(),
                contentDescription = cd,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    AlbumArtPlaceholder(
                        modifier = Modifier.fillMaxSize(),
                        fillMax = true,
                        cornerDp = 0.dp,
                    )
                },
                error = {
                    AlbumArtPlaceholder(
                        modifier = Modifier.fillMaxSize(),
                        fillMax = true,
                        cornerDp = 0.dp,
                    )
                }
            )
        } else {
            AlbumArtPlaceholder(
                modifier = Modifier.fillMaxSize(),
                fillMax = true,
                cornerDp = 0.dp,
            )
        }
    }
}

@Composable
fun TrackAlbumArtCircle(
    track: Music?,
    modifier: Modifier = Modifier,
    size: Dp,
    contentScale: ContentScale = ContentScale.Crop,
) {
    TrackAlbumArt(
        track = track,
        modifier = modifier,
        size = size,
        cornerDp = 8.dp,
        clipShape = CircleShape,
        contentScale = contentScale,
        highRes = true,
    )
}
