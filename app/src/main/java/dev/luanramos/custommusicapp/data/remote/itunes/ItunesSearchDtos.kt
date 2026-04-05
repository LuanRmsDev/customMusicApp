package dev.luanramos.custommusicapp.data.remote.itunes

import com.google.gson.annotations.SerializedName
import dev.luanramos.custommusicapp.domain.Music
import dev.luanramos.custommusicapp.domain.MusicArtwork

/**
 * Top-level JSON from `GET https://itunes.apple.com/search`.
 * See [Apple — Understanding Search Results](https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/UnderstandingSearchResults.html).
 */
data class ItunesSearchResponseDto(
    @SerializedName("resultCount") val resultCount: Int = 0,
    @SerializedName("results") val results: List<ItunesSearchResultDto> = emptyList(),
)

/**
 * One element of `results`. The API mixes **track**, **collection**, and **artist** objects with
 * different [wrapperType] / [kind] values; every field is optional and only those present in the JSON are set.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
data class ItunesSearchResultDto(
    // ——— Type ———
    @SerializedName("wrapperType") val wrapperType: String? = null,
    @SerializedName("kind") val kind: String? = null,
    // ——— Identifiers ———
    @SerializedName("artistId") val artistId: Long? = null,
    @SerializedName("collectionId") val collectionId: Long? = null,
    @SerializedName("trackId") val trackId: Long? = null,
    @SerializedName("amgArtistId") val amgArtistId: Long? = null,
    @SerializedName("amgAlbumId") val amgAlbumId: Long? = null,
    @SerializedName("amgVideoId") val amgVideoId: Long? = null,
    @SerializedName("collectionArtistId") val collectionArtistId: Long? = null,
    // ——— Names ———
    @SerializedName("artistName") val artistName: String? = null,
    @SerializedName("collectionName") val collectionName: String? = null,
    @SerializedName("trackName") val trackName: String? = null,
    @SerializedName("collectionArtistName") val collectionArtistName: String? = null,
    @SerializedName("collectionCensoredName") val collectionCensoredName: String? = null,
    @SerializedName("trackCensoredName") val trackCensoredName: String? = null,
    // ——— Store / preview URLs ———
    @SerializedName("artistViewUrl") val artistViewUrl: String? = null,
    @SerializedName("collectionViewUrl") val collectionViewUrl: String? = null,
    @SerializedName("trackViewUrl") val trackViewUrl: String? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null,
    @SerializedName("feedUrl") val feedUrl: String? = null,
    // ——— Artwork ———
    @SerializedName("artworkUrl30") val artworkUrl30: String? = null,
    @SerializedName("artworkUrl60") val artworkUrl60: String? = null,
    @SerializedName("artworkUrl100") val artworkUrl100: String? = null,
    @SerializedName("artworkUrl160") val artworkUrl160: String? = null,
    @SerializedName("artworkUrl600") val artworkUrl600: String? = null,
    // ——— Pricing (store; Double matches JSON decimals) ———
    @SerializedName("collectionPrice") val collectionPrice: Double? = null,
    @SerializedName("trackPrice") val trackPrice: Double? = null,
    @SerializedName("trackRentalPrice") val trackRentalPrice: Double? = null,
    @SerializedName("collectionHdPrice") val collectionHdPrice: Double? = null,
    @SerializedName("trackHdPrice") val trackHdPrice: Double? = null,
    @SerializedName("trackHdRentalPrice") val trackHdRentalPrice: Double? = null,
    // ——— Release & duration ———
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long? = null,
    // ——— Explicitness / ratings ———
    @SerializedName("collectionExplicitness") val collectionExplicitness: String? = null,
    @SerializedName("trackExplicitness") val trackExplicitness: String? = null,
    @SerializedName("contentAdvisoryRating") val contentAdvisoryRating: String? = null,
    // ——— Disc / track index ———
    @SerializedName("discCount") val discCount: Int? = null,
    @SerializedName("discNumber") val discNumber: Int? = null,
    @SerializedName("trackCount") val trackCount: Int? = null,
    @SerializedName("trackNumber") val trackNumber: Int? = null,
    // ——— Region & genre ———
    @SerializedName("country") val country: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("primaryGenreName") val primaryGenreName: String? = null,
    @SerializedName("genreIds") val genreIds: List<String>? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    // ——— Streaming / descriptions (present on some media kinds) ———
    @SerializedName("isStreamable") val isStreamable: Boolean? = null,
    @SerializedName("shortDescription") val shortDescription: String? = null,
    @SerializedName("longDescription") val longDescription: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("copyright") val copyright: String? = null,
)

/** Maps iTunes artwork fields into domain [MusicArtwork]; returns null if no URL is present. */
fun ItunesSearchResultDto.toMusicArtworkOrNull(): MusicArtwork? {
    val artwork = MusicArtwork(
        url30 = artworkUrl30.orBlankToNull(),
        url60 = artworkUrl60.orBlankToNull(),
        url100 = artworkUrl100.orBlankToNull(),
        url160 = artworkUrl160.orBlankToNull(),
        url600 = artworkUrl600.orBlankToNull(),
    )
    return artwork.takeIf { it.hasAnyUrl() }
}

private fun String?.orBlankToNull(): String? = this?.takeIf { it.isNotBlank() }

/** Maps a search result to [Music] when it is playable (has iTunes preview audio). */
fun ItunesSearchResultDto.toMusicOrNull(): Music? {
    val id = trackId?.takeIf { it > 0 } ?: return null
    val url = previewUrl?.takeIf { it.isNotBlank() } ?: return null
    return Music(
        id = id.toString(),
        title = trackName?.takeIf { it.isNotBlank() } ?: "Unknown track",
        artist = artistName?.takeIf { it.isNotBlank() } ?: "Unknown artist",
        songUrl = url,
        artwork = toMusicArtworkOrNull(),
    )
}
