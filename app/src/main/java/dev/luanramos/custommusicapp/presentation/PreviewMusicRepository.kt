package dev.luanramos.custommusicapp.presentation

import dev.luanramos.custommusicapp.data.mock.LibraryMockedData
import dev.luanramos.custommusicapp.domain.model.AlbumDetail
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository

/** Static browse data for @Preview screens (no network / DB). */
internal object PreviewMusicRepository : MusicRepository {
    override suspend fun getPopularSongs(limit: Int, offset: Int): List<Music> =
        LibraryMockedData.songs.take(limit)

    override suspend fun getLastPlayedSongs(limit: Int, offset: Int): List<Music> =
        LibraryMockedData.songs.take(3)

    override suspend fun searchSong(searchTerm: String, limit: Int, offset: Int): List<Music> {
        val q = searchTerm.trim()
        if (q.isEmpty()) return emptyList()
        return LibraryMockedData.songs.filter { m ->
            m.title.contains(q, ignoreCase = true) || m.artist.contains(q, ignoreCase = true)
        }.take(limit)
    }

    override suspend fun getAlbumDetail(anchor: Music): AlbumDetail? =
        AlbumDetail(
            title = LibraryMockedData.sampleDisplayAlbumTitle,
            artistName = LibraryMockedData.sampleDisplayAlbumArtist,
            tracks = LibraryMockedData.sampleDisplayAlbumTracks,
        )

    override suspend fun saveSong(music: Music) = Unit

    override suspend fun clearAllCache() = Unit
}
