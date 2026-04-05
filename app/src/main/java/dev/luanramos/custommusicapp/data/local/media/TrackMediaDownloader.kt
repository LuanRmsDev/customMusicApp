package dev.luanramos.custommusicapp.data.local.media

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import dev.luanramos.custommusicapp.data.local.db.entity.TrackEntity
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

@Singleton
class TrackMediaDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val dao: MusicLibraryDao,
) {
    private val audioDir = File(context.filesDir, "audio_cache").apply { mkdirs() }
    private val artworkDir = File(context.filesDir, "artwork_cache").apply { mkdirs() }
    private val concurrency = Semaphore(2)

    /** Prefer on-disk preview; fall back to remote [Music.songUrl]. */
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
     * Downloads preview audio and best-effort artwork when missing, then updates Room.
     * Safe to call repeatedly; skips existing valid files.
     */
    suspend fun downloadMissingFiles(trackId: String) =
        concurrency.withPermit {
            withContext(Dispatchers.IO) {
                val entity = dao.getTrack(trackId) ?: return@withContext
                var current = entity

                if (needsAudioDownload(current)) {
                    val url = current.songUrl?.takeIf { it.isNotBlank() }
                    if (url != null) {
                        val out = File(audioDir, "${safeFileName(trackId)}.m4a")
                        if (downloadToFile(url, out)) {
                            current = current.copy(localAudioPath = out.absolutePath)
                        }
                    }
                }

                if (needsArtworkDownload(current)) {
                    val artUrl = pickArtworkUrl(current)
                    if (artUrl != null) {
                        val out = File(artworkDir, "${safeFileName(trackId)}.jpg")
                        if (downloadToFile(artUrl, out)) {
                            current = current.copy(localArtworkPath = out.absolutePath)
                        }
                    }
                }

                if (current != entity) {
                    dao.updateTrack(current)
                }
            }
        }

    private fun needsAudioDownload(e: TrackEntity): Boolean {
        val p = e.localAudioPath
        if (p.isNullOrBlank()) return e.songUrl != null
        val f = File(p)
        return !f.isFile || f.length() == 0L
    }

    private fun needsArtworkDownload(e: TrackEntity): Boolean {
        if (pickArtworkUrl(e) == null) return false
        val p = e.localArtworkPath
        if (p.isNullOrBlank()) return true
        val f = File(p)
        return !f.isFile || f.length() == 0L
    }

    private fun pickArtworkUrl(e: TrackEntity): String? =
        listOf(
            e.artworkUrl600,
            e.artworkUrl160,
            e.artworkUrl100,
            e.artworkUrl60,
            e.artworkUrl30,
        ).firstOrNull { !it.isNullOrBlank() }

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
}
