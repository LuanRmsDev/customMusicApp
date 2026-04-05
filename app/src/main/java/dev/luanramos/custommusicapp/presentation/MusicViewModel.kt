package dev.luanramos.custommusicapp.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    val playback: TrackPlaybackController
) : ViewModel() {

    override fun onCleared() {
        playback.release()
        super.onCleared()
    }
}
