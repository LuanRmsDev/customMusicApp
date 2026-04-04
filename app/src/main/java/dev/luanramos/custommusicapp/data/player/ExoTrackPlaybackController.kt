package dev.luanramos.custommusicapp.data.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.update

@Singleton
class ExoTrackPlaybackController @Inject constructor(
    @ApplicationContext context: Context
) : TrackPlaybackController {

    private val appContext = context

    private val _state = MutableStateFlow(TrackPlaybackState())
    override val state: StateFlow<TrackPlaybackState> = _state.asStateFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(appContext).build().apply {
        addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING ->
                            _state.update { it.copy(isBuffering = true) }
                        Player.STATE_READY ->
                            _state.update { it.copy(isBuffering = false) }
                        Player.STATE_ENDED ->
                            _state.update { it.copy(isPlaying = false, isBuffering = false) }
                        Player.STATE_IDLE ->
                            _state.update {
                                it.copy(isPlaying = false, isBuffering = false)
                            }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update { it.copy(isPlaying = isPlaying, isBuffering = false) }
                }

                override fun onPlayerError(error: PlaybackException) {
                    _state.update {
                        it.copy(
                            isPlaying = false,
                            isBuffering = false,
                            errorMessage = error.message
                        )
                    }
                }
            }
        )
    }

    override fun play(track: Music) {
        val url = track.songUrl
        if (url == null) {
            player.stop()
            player.clearMediaItems()
            _state.value = TrackPlaybackState(
                currentTrack = track,
                isPlaying = false,
                isBuffering = false,
                errorMessage = null
            )
            return
        }
        _state.update {
            TrackPlaybackState(
                currentTrack = track,
                isPlaying = false,
                isBuffering = true,
                errorMessage = null
            )
        }
        player.setMediaItem(MediaItem.fromUri(url))
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
        player.stop()
        player.clearMediaItems()
        _state.value = TrackPlaybackState()
    }

    override fun release() {
        player.release()
        _state.value = TrackPlaybackState()
    }
}
