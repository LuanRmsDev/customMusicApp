package dev.luanramos.custommusicapp.domain.model

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

    /**
     * Picks the smallest iTunes-sized asset whose edge length is at least [maxEdgePx], else the largest available.
     */
    fun bestUrlForMaxEdgePx(maxEdgePx: Int): String? {
        val pairs = listOf(
            30 to url30,
            60 to url60,
            100 to url100,
            160 to url160,
            600 to url600,
        ).mapNotNull { (dim, u) ->
            u?.takeIf { it.isNotBlank() }?.let { dim to it }
        }
        if (pairs.isEmpty()) return null
        val need = maxEdgePx.coerceAtLeast(30)
        return pairs.filter { it.first >= need }
            .minByOrNull { it.first }
            ?.second
            ?: pairs.maxByOrNull { it.first }!!.second
    }
}
