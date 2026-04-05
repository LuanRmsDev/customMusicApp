package dev.luanramos.custommusicapp.domain

/**
 * Album / track artwork at multiple resolutions (iTunes Search `artworkUrl30` … `artworkUrl600`).
 */
data class MusicArtwork(
    val url30: String? = null,
    val url60: String? = null,
    val url100: String? = null,
    val url160: String? = null,
    val url600: String? = null,
) {
    val preferredDisplayUrl: String?
        get() = url600 ?: url160 ?: url100 ?: url60 ?: url30

    fun hasAnyUrl(): Boolean =
        !url30.isNullOrBlank() ||
            !url60.isNullOrBlank() ||
            !url100.isNullOrBlank() ||
            !url160.isNullOrBlank() ||
            !url600.isNullOrBlank()
}
