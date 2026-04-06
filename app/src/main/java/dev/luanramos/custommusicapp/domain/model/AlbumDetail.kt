package dev.luanramos.custommusicapp.domain.model

data class AlbumDetail(
    val title: String,
    val artistName: String,
    val tracks: List<Music>,
)
