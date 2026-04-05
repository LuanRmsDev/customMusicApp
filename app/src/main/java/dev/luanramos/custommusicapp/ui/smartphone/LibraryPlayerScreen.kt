package dev.luanramos.custommusicapp.ui.smartphone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.luanramos.custommusicapp.R
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.presentation.MusicViewModel
import dev.luanramos.custommusicapp.presentation.PreviewMusicRepository
import dev.luanramos.custommusicapp.ui.components.PlayerAlbumArtSection
import dev.luanramos.custommusicapp.ui.components.PlayerBufferingIndicator
import dev.luanramos.custommusicapp.ui.components.PlayerErrorMessage
import dev.luanramos.custommusicapp.ui.components.PlayerMenuBottomSheet
import dev.luanramos.custommusicapp.ui.components.PlayerScreenTopBar
import dev.luanramos.custommusicapp.ui.components.PlayerSeekBar
import dev.luanramos.custommusicapp.ui.components.PlayerTrackHeader
import dev.luanramos.custommusicapp.ui.components.PlayerTransportControls
import dev.luanramos.custommusicapp.ui.theme.CustomMusicAppTheme
import dev.luanramos.custommusicapp.ui.util.canPlayAudio
import dev.luanramos.custommusicapp.ui.util.isCompactPhoneLandscape

@Composable
fun LibraryPlayerScreen(
    viewModel: MusicViewModel,
    onPlayerMenuViewAlbum: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val state = ui.playbackState
    val track = state.currentTrack
    val canPlay = track.canPlayAudio()
    val durationMs = state.durationMs
    val positionMs = state.positionMs

    var repeatOn by rememberSaveable { mutableStateOf(false) }
    var showMenuSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(track?.id) {
        if (track == null) showMenuSheet = false
    }

    val phoneLandscape = isCompactPhoneLandscape()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            PlayerScreenTopBar(
                title = stringResource(R.string.player_now_playing_title),
                onBack = onBack,
                onMore = { if (track != null) showMenuSheet = true },
                moreEnabled = track != null
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .then(
                        if (phoneLandscape) {
                            Modifier.verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                        }
                    ),
                verticalArrangement = if (phoneLandscape) {
                    Arrangement.spacedBy(16.dp)
                } else {
                    Arrangement.SpaceBetween
                }
            ) {
                if (!phoneLandscape) {
                    PlayerAlbumArtSection()
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PlayerTrackHeader(
                        title = track?.title,
                        artist = track?.artist,
                        emptyLabel = stringResource(R.string.player_no_track)
                    )

                    state.errorMessage?.let { PlayerErrorMessage(message = it) }

                    if (track != null) {
                        PlayerSeekBar(
                            positionMs = positionMs,
                            durationMs = durationMs,
                            onSeekToFraction = { v ->
                                if (durationMs > 0) {
                                    viewModel.seekTo((v * durationMs).toLong())
                                }
                            },
                            enabled = canPlay && durationMs > 0 && !state.isBuffering
                        )
                    }

                    PlayerBufferingIndicator(visible = state.isBuffering)

                    if (track != null) {
                        PlayerTransportControls(
                            isPlaying = state.isPlaying,
                            canPlay = canPlay,
                            isBuffering = state.isBuffering,
                            repeatOn = repeatOn,
                            onPlayPause = {
                                if (state.isPlaying) viewModel.pause() else viewModel.resume()
                            },
                            onSkipPrevious = { viewModel.skipToPrevious() },
                            onSkipNext = { viewModel.skipToNext() },
                            onRepeatToggle = { repeatOn = !repeatOn }
                        )
                    }
                }
            }
        }

        if (showMenuSheet && track != null) {
            PlayerMenuBottomSheet(
                onDismiss = { showMenuSheet = false },
                songTitle = track.title,
                artistName = track.artist,
                onViewAlbumClick = onPlayerMenuViewAlbum
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
private fun LibraryPlayerScreenPreview() {
    val vm = MusicViewModel(PreviewMusicRepository, FakeTrackPlaybackController)
    LaunchedEffect(Unit) {
        FakeTrackPlaybackController.stop()
        vm.playTrack(
            Music(
                id = "preview",
                title = "Get Lucky",
                artist = "Daft Punk feat. Pharrell Williams",
                songUrl = "https://example.com/preview.mp3"
            )
        )
    }
    CustomMusicAppTheme {
        LibraryPlayerScreen(
            viewModel = vm,
            onPlayerMenuViewAlbum = {},
            onBack = {}
        )
    }
}
