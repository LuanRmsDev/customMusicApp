package dev.luanramos.custommusicapp.data.repository

import dev.luanramos.custommusicapp.data.local.db.MusicLibraryDao
import dev.luanramos.custommusicapp.data.local.db.toMusic
import dev.luanramos.custommusicapp.data.local.media.TrackMediaDownloader
import dev.luanramos.custommusicapp.data.remote.itunes.ItunesSearchApi
import dev.luanramos.custommusicapp.data.remote.itunes.toMusicOrNull
import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val itunesSearchApi: ItunesSearchApi,
    private val musicLibraryDao: MusicLibraryDao,
    private val trackMediaDownloader: TrackMediaDownloader,
) : MusicRepository {

    override suspend fun getPopularSongs(limit: Int, offset: Int): List<Music> =
        withContext(Dispatchers.IO) {
            runCatching {
                itunesSearchApi.search(
                    term = MusicRepository.POPULAR_ITUNES_TERM,
                    media = "music",
                    limit = limit.coerceIn(1, 200),
                    offset = offset.coerceAtLeast(0),
                )
            }.getOrNull()
                ?.results
                .orEmpty()
                .mapNotNull { it.toMusicOrNull() }
        }

    override suspend fun getLastPlayedSongs(limit: Int, offset: Int): List<Music> =
        withContext(Dispatchers.IO) {
            musicLibraryDao
                .getRecentTracks(
                    limit = limit.coerceIn(1, 500),
                    offset = offset.coerceAtLeast(0),
                )
                .map { it.toMusic() }
        }

    override suspend fun searchSong(searchTerm: String, limit: Int, offset: Int): List<Music> =
        withContext(Dispatchers.IO) {
            val term = searchTerm.trim()
            if (term.isEmpty()) {
                return@withContext emptyList()
            }
            runCatching {
                itunesSearchApi.search(
                    term = term,
                    media = "music",
                    limit = limit.coerceIn(1, 200),
                    offset = offset.coerceAtLeast(0),
                )
            }.getOrNull()
                ?.results
                .orEmpty()
                .mapNotNull { it.toMusicOrNull() }
        }

    override suspend fun saveSong(music: Music) =
        withContext(Dispatchers.IO) {
            val toStore = trackMediaDownloader.downloadForPersistence(music)
            musicLibraryDao.saveSong(toStore)
        }

    override suspend fun clearAllCache() =
        withContext(Dispatchers.IO) {
            musicLibraryDao.deleteAllTracks()
            trackMediaDownloader.clearCacheDirectories()
        }
}
