package dev.luanramos.custommusicapp.ui.util

import android.content.Context
import dev.luanramos.custommusicapp.data.util.checkInternetConnection
import dev.luanramos.custommusicapp.domain.model.Music
import java.io.File

/**
 * Resolves what Coil should load: when offline, prefer [Music.localArtworkPath]; when online, prefer remote
 * artwork at a resolution near [targetEdgePx], falling back to local file or disk cache.
 */
fun resolveAlbumArtLoadData(
    music: Music?,
    targetEdgePx: Int,
    context: Context,
): Any? {
    if (music == null) return null
    val local = music.localArtworkPath
        ?.let { File(it) }
        ?.takeIf { it.isFile && it.length() > 0L }
    val url = music.artwork?.bestUrlForMaxEdgePx(targetEdgePx.coerceIn(30, 1200))
    val online = checkInternetConnection(context)
    return when {
        !online -> local ?: url
        else -> url ?: local
    }
}
