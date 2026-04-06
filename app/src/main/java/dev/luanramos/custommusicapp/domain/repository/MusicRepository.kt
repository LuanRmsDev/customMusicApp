package dev.luanramos.custommusicapp.domain.repository

import dev.luanramos.custommusicapp.domain.model.AlbumDetail
import dev.luanramos.custommusicapp.domain.model.Music

interface MusicRepository {

    /**
     * Loads one page of popular tracks from the **iTunes Search API only**.
     *
     * @param limit Number of results to request (typically [DEFAULT_PAGE_SIZE]).
     * @param offset iTunes pagination offset into the result set for this query.
     * @return Mapped playable tracks, or an empty list if the request fails or returns no playable items.
     */
    suspend fun getPopularSongs(limit: Int = DEFAULT_PAGE_SIZE, offset: Int = 0): List<Music>

    /**
     * Loads one page of the user’s recently played tracks from **local storage only**
     * (tracks with a non-null [Music.lastPlayedAt]), newest first.
     *
     * @param limit Max rows to return.
     * @param offset SQL-style offset into the ordered history.
     * @return Empty list when there is no play history.
     */
    suspend fun getLastPlayedSongs(limit: Int = DEFAULT_PAGE_SIZE, offset: Int = 0): List<Music>

    /**
     * Searches the **iTunes Search API only** (no local DB read/write).
     *
     * @param searchTerm Free-text query; blank/whitespace-only returns an empty list without calling the API.
     * @param limit Number of results to request.
     * @param offset iTunes pagination offset.
     */
    suspend fun searchSong(
        searchTerm: String,
        limit: Int = DEFAULT_PAGE_SIZE,
        offset: Int = 0,
    ): List<Music>

    /**
     * Loads album metadata and playable tracks via iTunes **lookup**: prefers [Music.amgAlbumId]
     * (`amgAlbumId=…&entity=song`), otherwise [Music.collectionId] (`id=_collectionId_&entity=song`).
     * Returns `null` when both ids are missing or the request / mapping yields no playable tracks.
     */
    suspend fun getAlbumDetail(anchor: Music): AlbumDetail?

    /**
     * **Inserts or updates the full track row** in the library DB: downloads preview audio and artwork when URLs are present,
     * stores local file paths, persists iTunes artwork URLs, omits the preview stream URL, and sets **last played** to now for this save.
     *
     * Ongoing playback should only adjust **play count** and **last played** (e.g. via `TrackPlayRecorder`), not re-call this method.
     */
    suspend fun saveSong(music: Music)

    /** Deletes all persisted tracks and removes downloaded audio/artwork cache files from app storage. */
    suspend fun clearAllCache()

    companion object {
        /** Suggested page size for API calls (Apple allows up to 200 per request). */
        const val DEFAULT_PAGE_SIZE: Int = 25

        /** Fixed query used for “popular” browsing via the Search API (there is no public charts endpoint). */
        const val POPULAR_ITUNES_TERM: String = "pop hits"

        /** Practical upper bound for `offset` + `limit` per iTunes search query. */
        const val MAX_ITUNES_OFFSET: Int = 200
    }
}
