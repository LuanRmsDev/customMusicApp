package dev.luanramos.custommusicapp.data.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.util.checkInternetConnection
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackState
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class ExoTrackPlaybackController @Inject constructor(
    @ApplicationContext context: Context,
) : TrackPlaybackController {

    private val appContext = context

    private val _state = MutableStateFlow(TrackPlaybackState())
    override val state: StateFlow<TrackPlaybackState> = _state.asStateFlow()

    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressTick = object : Runnable {
        override fun run() {
            val duration = player.duration
            if (duration > 0) {
                _state.update {
                    it.copy(
                        positionMs = player.currentPosition,
                        durationMs = duration
                    )
                }
            }
            if (player.isPlaying) {
                progressHandler.postDelayed(this, 500L)
            }
        }
    }

    private val player: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING ->
                            _state.update { it.copy(isBuffering = true) }
                        Player.STATE_READY -> {
                            val d = player.duration
                            _state.update {
                                it.copy(
                                    isBuffering = false,
                                    durationMs = if (d > 0) d else it.durationMs,
                                    positionMs = player.currentPosition
                                )
                            }
                            if (player.isPlaying) startProgressUpdates()
                        }
                        Player.STATE_ENDED -> {
                            stopProgressUpdates()
                            _state.update {
                                it.copy(
                                    isPlaying = false,
                                    isBuffering = false,
                                    positionMs = it.durationMs
                                )
                            }
                        }
                        Player.STATE_IDLE ->
                            _state.update {
                                it.copy(isPlaying = false, isBuffering = false)
                            }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update { it.copy(isPlaying = isPlaying, isBuffering = false) }
                    if (isPlaying) startProgressUpdates() else stopProgressUpdates()
                }

                override fun onPlayerError(error: PlaybackException) {
                    stopProgressUpdates()
                    _state.update {
                        it.copy(
                            isPlaying = false,
                            isBuffering = false,
                            errorMessage = error.message
                                ?: appContext.getString(R.string.playback_error_no_playable_source)
                        )
                    }
                }
            }
        )
    }

    private fun startProgressUpdates() {
        progressHandler.removeCallbacks(progressTick)
        progressHandler.post(progressTick)
    }

    private fun stopProgressUpdates() {
        progressHandler.removeCallbacks(progressTick)
    }

    private fun remoteUri(track: Music): Uri? {
        val url = track.songUrl?.takeIf { it.isNotBlank() } ?: return null
        return Uri.parse(url)
    }

    private fun localUri(track: Music): Uri? {
        val path = track.localAudioPath?.takeIf { it.isNotBlank() } ?: return null
        val file = File(path)
        return if (file.isFile && file.length() > 0L) Uri.fromFile(file) else null
    }

    /**
     * When online, prefer streaming [Music.songUrl]; otherwise use a valid local file.
     * When offline, only a non-empty local file can be played.
     */
    private fun resolvePlaybackUri(track: Music): Uri? =
        if (checkInternetConnection(appContext)) {
            remoteUri(track) ?: localUri(track)
        } else {
            localUri(track)
        }

    override fun play(track: Music) {
        stopProgressUpdates()
        val uri = resolvePlaybackUri(track)
        if (uri == null) {
            player.stop()
            player.clearMediaItems()
            _state.value = TrackPlaybackState(
                currentTrack = track,
                isPlaying = false,
                isBuffering = false,
                errorMessage = appContext.getString(R.string.playback_error_no_playable_source),
                positionMs = 0L,
                durationMs = 0L
            )
            return
        }
        _state.update {
            TrackPlaybackState(
                currentTrack = track,
                isPlaying = false,
                isBuffering = true,
                errorMessage = null,
                positionMs = 0L,
                durationMs = 0L
            )
        }
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        if (player.mediaItemCount > 0) {
            player.play()
        }
    }

    override fun stop() {
        stopProgressUpdates()
        player.stop()
        player.clearMediaItems()
        _state.value = TrackPlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        val d = when {
            player.duration > 0 -> player.duration
            _state.value.durationMs > 0 -> _state.value.durationMs
            else -> 0L
        }
        val coerced = if (d > 0) positionMs.coerceIn(0L, d) else positionMs.coerceAtLeast(0L)
        player.seekTo(coerced)
        _state.update {
            it.copy(
                positionMs = coerced,
                durationMs = if (player.duration > 0) player.duration else it.durationMs
            )
        }
    }

    override fun skipToPrevious() {
        player.seekTo(0L)
        _state.update { it.copy(positionMs = 0L) }
    }

    override fun skipToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    override fun setRepeatOne(enabled: Boolean) {
        player.repeatMode =
            if (enabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    override fun release() {
        stopProgressUpdates()
        player.release()
        _state.value = TrackPlaybackState()
    }
}
