package dev.luanramos.custommusicapp.data.local.media

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import dev.luanramos.custommusicapp.data.local.db.toMusic
import dev.luanramos.custommusicapp.domain.model.Music
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Downloads preview audio and artwork from **transient** URLs on [Music] into app-private storage.
 * Does **not** write to Room; callers persist after download (e.g. repository `saveSong`).
 */
@Singleton
class TrackMediaDownloader @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val dao: MusicLibraryDao,
) {
    private val audioDir = File(context.filesDir, "audio_cache").apply { mkdirs() }
    private val artworkDir = File(context.filesDir, "artwork_cache").apply { mkdirs() }
    private val concurrency = Semaphore(2)

    /**
     * Local file URI if cached audio exists; otherwise streaming [Music.songUrl] (e.g. API results not saved yet).
     */
    fun playbackUri(music: Music): Uri? {
        val local = music.localAudioPath
        if (!local.isNullOrBlank()) {
            val f = File(local)
            if (f.isFile && f.length() > 0L) {
                return Uri.fromFile(f)
            }
        }
        val url = music.songUrl?.takeIf { it.isNotBlank() } ?: return null
        return Uri.parse(url)
    }

    /**
     * Downloads audio/art from [source]’s remote fields, merges with any existing row for play stats / paths,
     * and returns a [Music] suitable for DB: **no** preview [Music.songUrl]; [Music.artwork] URLs are kept
     * (merged from [source] or any existing row) for persistence.
     */
    suspend fun downloadForPersistence(source: Music): Music =
        concurrency.withPermit {
            withContext(Dispatchers.IO) {
                val stored = dao.getTrack(source.id)?.toMusic()

                var audioPath = source.localAudioPath ?: stored?.localAudioPath
                var artworkPath = source.localArtworkPath ?: stored?.localArtworkPath

                val audioUrl = source.songUrl?.takeIf { it.isNotBlank() }
                if (audioUrl != null && shouldRedownload(audioPath)) {
                    val out = File(audioDir, "${safeFileName(source.id)}.m4a")
                    if (downloadToFile(audioUrl, out)) {
                        audioPath = out.absolutePath
                    }
                }

                val artUrl = source.artwork?.preferredDisplayUrl?.takeIf { it.isNotBlank() }
                if (artUrl != null && shouldRedownload(artworkPath)) {
                    val ext = extensionForImageUrl(artUrl)
                    val out = File(artworkDir, "${safeFileName(source.id)}.$ext")
                    if (downloadToFile(artUrl, out)) {
                        artworkPath = out.absolutePath
                    }
                }

                val artworkForDb = source.artwork ?: stored?.artwork
                Music(
                    id = source.id,
                    title = source.title,
                    artist = source.artist,
                    songUrl = null,
                    artwork = artworkForDb,
                    playCount = stored?.playCount ?: source.playCount,
                    lastPlayedAt = stored?.lastPlayedAt ?: source.lastPlayedAt,
                    localAudioPath = audioPath,
                    localArtworkPath = artworkPath,
                )
            }
        }

    /** Deletes all files under [audioDir] and [artworkDir]. */
    fun clearCacheDirectories() {
        audioDir.listFiles()?.forEach { it.deleteRecursively() }
        artworkDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    private fun shouldRedownload(path: String?): Boolean {
        if (path.isNullOrBlank()) return true
        val f = File(path)
        return !f.isFile || f.length() == 0L
    }

    private fun downloadToFile(url: String, outFile: File): Boolean {
        return runCatching {
            outFile.parentFile?.mkdirs()
            val tmp = File(outFile.parentFile, "${outFile.name}.tmp")
            tmp.delete()
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false
                val body = response.body ?: return false
                FileOutputStream(tmp).use { stream ->
                    body.byteStream().use { input -> input.copyTo(stream) }
                }
                if (tmp.length() <= 0L) return false
                if (outFile.exists()) outFile.delete()
                tmp.renameTo(outFile) && outFile.length() > 0L
            }
        }.getOrDefault(false)
    }

    private fun safeFileName(id: String): String =
        id.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(120)

    private fun extensionForImageUrl(url: String): String {
        val path = url.substringBefore('?').lowercase()
        return when {
            path.endsWith(".png") -> "png"
            path.endsWith(".webp") -> "webp"
            else -> "jpg"
        }
    }
}
