package dev.luanramos.custommusicapp.domain

data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val songUrl: String? = null
)
