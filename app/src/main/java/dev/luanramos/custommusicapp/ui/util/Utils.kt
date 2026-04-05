package dev.luanramos.custommusicapp.ui.util

fun formatPlaybackTimeMs(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val m = totalSec / 60L
    val s = totalSec % 60L
    return "$m:${s.toString().padStart(2, '0')}"
}

/** Android Auto style elapsed / duration, e.g. `1:19/3:26`. */
fun formatPlaybackElapsedSlashTotal(positionMs: Long, durationMs: Long): String =
    "${formatPlaybackTimeMs(positionMs)}/${formatPlaybackTimeMs(durationMs)}"