package dev.luanramos.custommusicapp.ui.util

import dev.luanramos.custommusicapp.domain.model.Music

fun Music?.canPlayAudio(): Boolean =
    this != null &&
        (!songUrl.isNullOrBlank() || !localAudioPath.isNullOrBlank())
