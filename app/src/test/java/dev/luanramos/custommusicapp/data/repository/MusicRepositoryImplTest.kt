package dev.luanramos.custommusicapp.data.repository

import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import dev.luanramos.custommusicapp.data.local.db.entity.TrackEntity
import dev.luanramos.custommusicapp.data.local.media.TrackMediaDownloader
import dev.luanramos.custommusicapp.data.remote.itunes.ItunesSearchApi
import dev.luanramos.custommusicapp.data.remote.itunes.ItunesSearchResponseDto
import dev.luanramos.custommusicapp.data.remote.itunes.ItunesSearchResultDto
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MusicRepositoryImplTest {

    private val itunesSearchApi: ItunesSearchApi = mockk()
    private val musicLibraryDao: MusicLibraryDao = mockk(relaxed = true)
    private val trackMediaDownloader: TrackMediaDownloader = mockk(relaxed = true)

    private lateinit var repository: MusicRepositoryImpl

    @Before
    fun setup() {
        repository = MusicRepositoryImpl(itunesSearchApi, musicLibraryDao, trackMediaDownloader)
    }

    @Test
    fun `getPopularSongs maps search results and respects limit`() = runBlocking {
        val dtos =
            (1L..5L).map { id ->
                playableTrackDto(trackId = id, previewUrl = "https://p.example/$id.m4a")
            }
        coEvery {
            itunesSearchApi.search(
                term = MusicRepository.POPULAR_ITUNES_TERM,
                media = "music",
                limit = MusicRepository.MAX_ITUNES_SEARCH_LIMIT,
            )
        } returns ItunesSearchResponseDto(results = dtos)

        val result = repository.getPopularSongs(limit = 2)

        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("2", result[1].id)
    }

    @Test
    fun `getPopularSongs returns empty when API throws`() = runBlocking {
        coEvery {
            itunesSearchApi.search(any(), any(), any(), any())
        } throws RuntimeException("network")

        val result = repository.getPopularSongs(limit = 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchSong returns empty without calling API for blank term`() = runBlocking {
        val result = repository.searchSong("   ", limit = 10)

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { itunesSearchApi.search(any(), any(), any(), any()) }
    }

    @Test
    fun `searchSong maps results and trims term`() = runBlocking {
        val dto = playableTrackDto(99L, "Song", "Artist")
        coEvery {
            itunesSearchApi.search(
                term = "beatles",
                media = "music",
                limit = MusicRepository.MAX_ITUNES_SEARCH_LIMIT,
            )
        } returns ItunesSearchResponseDto(results = listOf(dto))

        val result = repository.searchSong("  beatles  ", limit = 50)

        assertEquals(1, result.size)
        assertEquals("99", result[0].id)
        assertEquals("Song", result[0].title)
    }

    @Test
    fun `getLastPlayedSongs delegates to DAO with coerced args`() = runBlocking {
        val entity =
            TrackEntity(
                id = "e1",
                title = "T",
                artist = "A",
                albumTitle = null,
                amgAlbumId = null,
                collectionId = null,
                songUrl = null,
                artworkUrl30 = null,
                artworkUrl60 = null,
                artworkUrl100 = null,
                artworkUrl160 = null,
                artworkUrl600 = null,
                lastPlayedAt = 1L,
            )
        coEvery { musicLibraryDao.getRecentTracks(limit = 20, offset = 0) } returns listOf(entity)

        val result = repository.getLastPlayedSongs(limit = 20, offset = 0)

        assertEquals(1, result.size)
        assertEquals("e1", result[0].id)
        coVerify { musicLibraryDao.getRecentTracks(limit = 20, offset = 0) }
    }

    @Test
    fun `saveSong downloads then persists`() = runBlocking {
        val music =
            Music(
                id = "1",
                title = "t",
                artist = "a",
                songUrl = "https://p.example/x.m4a",
            )
        val stored = music.copy(localAudioPath = "/local")
        coEvery { trackMediaDownloader.downloadForPersistence(music) } returns stored

        repository.saveSong(music)

        coVerify { trackMediaDownloader.downloadForPersistence(music) }
        coVerify { musicLibraryDao.saveSong(stored) }
    }

    @Test
    fun `clearAllCache clears dao and downloader`() = runBlocking {
        repository.clearAllCache()

        coVerify { musicLibraryDao.deleteAllTracks() }
        verify { trackMediaDownloader.clearCacheDirectories() }
    }

    @Test
    fun `getAlbumDetail returns null when anchor has no album ids`() = runBlocking {
        val anchor = Music(id = "1", title = "t", artist = "a")

        assertNull(repository.getAlbumDetail(anchor))
    }

    @Test
    fun `getAlbumDetail uses lookup by collection id`() = runBlocking {
        val collection =
            ItunesSearchResultDto(
                wrapperType = "collection",
                collectionId = 100L,
                collectionName = "Album Name",
                artistName = "Band",
            )
        val song =
            playableTrackDto(
                trackId = 1L,
                trackName = "Track 1",
                artistName = "Band",
                collectionId = 100L,
                previewUrl = "https://p.example/1.m4a",
            )
        val response = ItunesSearchResponseDto(results = listOf(collection, song))
        coEvery { itunesSearchApi.lookupByCollectionId(100L) } returns response

        val anchor =
            Music(
                id = "1",
                title = "Track 1",
                artist = "Band",
                collectionId = 100L,
            )

        val detail = repository.getAlbumDetail(anchor)

        assertEquals("Album Name", detail?.title)
        assertEquals("Band", detail?.artistName)
        assertEquals(1, detail?.tracks?.size)
        coVerify { itunesSearchApi.lookupByCollectionId(100L) }
        coVerify(exactly = 0) { itunesSearchApi.lookupByAmgAlbumId(any()) }
    }

    @Test
    fun `getAlbumDetail prefers amg lookup when present`() = runBlocking {
        val song =
            playableTrackDto(
                trackId = 2L,
                trackName = "S",
                artistName = "A",
                previewUrl = "https://p.example/2.m4a",
            )
        val response = ItunesSearchResponseDto(results = listOf(song))
        coEvery { itunesSearchApi.lookupByAmgAlbumId(7L) } returns response

        val anchor =
            Music(
                id = "1",
                title = "S",
                artist = "A",
                amgAlbumId = 7L,
                collectionId = 99L,
            )

        val detail = repository.getAlbumDetail(anchor)

        assertEquals(1, detail?.tracks?.size)
        coVerify { itunesSearchApi.lookupByAmgAlbumId(7L) }
        coVerify(exactly = 0) { itunesSearchApi.lookupByCollectionId(any()) }
    }

    private fun playableTrackDto(
        trackId: Long,
        trackName: String = "Track",
        artistName: String = "Artist",
        collectionId: Long? = null,
        previewUrl: String = "https://p.example/$trackId.m4a",
    ): ItunesSearchResultDto =
        ItunesSearchResultDto(
            wrapperType = "track",
            kind = "song",
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            collectionId = collectionId,
            previewUrl = previewUrl,
        )
}
