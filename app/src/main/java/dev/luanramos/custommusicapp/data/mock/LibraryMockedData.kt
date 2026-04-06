package dev.luanramos.custommusicapp.data.mock

import dev.luanramos.custommusicapp.domain.model.Music
import dev.luanramos.custommusicapp.domain.model.MusicArtwork

object LibraryMockedData {

    private fun samplePreview(index: Int): String =
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$index.mp3"

    private fun mockArtwork(seed: String): MusicArtwork =
        MusicArtwork(
            url30 = "https://picsum.photos/seed/${seed}30/30/30",
            url60 = "https://picsum.photos/seed/${seed}60/60/60",
            url100 = "https://picsum.photos/seed/${seed}100/100/100",
            url160 = "https://picsum.photos/seed/${seed}160/160/160",
            url600 = "https://picsum.photos/seed/${seed}600/600/600",
        )

    /**
     * Sample album matching.
     */
    val sampleDisplayAlbumTitle: String = "Album Title"
    val sampleDisplayAlbumArtist: String = "Daft Punk"
    val sampleDisplayAlbumTracks: List<Music> = listOf(
        Music(
            id = "album-mock-1",
            title = "Around the World",
            artist = "Daft Punk",
            songUrl = samplePreview(10),
            artwork = mockArtwork("alb1"),
        ),
        Music(
            id = "album-mock-2",
            title = "Aerodynamic",
            artist = "Daft Punk",
            songUrl = samplePreview(11),
            artwork = mockArtwork("alb2"),
        ),
        Music(
            id = "album-mock-3",
            title = "Harder, Better, Faster, Stronger",
            artist = "Daft Punk",
            songUrl = samplePreview(12),
            artwork = mockArtwork("alb3"),
        ),
        Music(
            id = "album-mock-4",
            title = "Get Lucky",
            artist = "Daft Punk feat. Pharrell Williams",
            songUrl = samplePreview(5),
            artwork = mockArtwork("alb4"),
        ),
        Music(
            id = "album-mock-5",
            title = "Digital Love",
            artist = "Daft Punk",
            songUrl = samplePreview(13),
            artwork = mockArtwork("alb5"),
        ),
        Music(
            id = "album-mock-6",
            title = "One More Time",
            artist = "Daft Punk",
            songUrl = samplePreview(14),
            artwork = mockArtwork("alb6"),
        )
    )

    val songs: List<Music> = listOf(
        Music(
            id = "mock-1",
            title = "Purple Rain",
            artist = "Prince",
            songUrl = samplePreview(1),
            artwork = mockArtwork("m1"),
        ),
        Music(
            id = "mock-2",
            title = "Power Of Equality",
            artist = "Red Hot Chili Peppers",
            songUrl = samplePreview(2),
            artwork = mockArtwork("m2"),
        ),
        Music(
            id = "mock-3",
            title = "Something",
            artist = "The Beatles",
            songUrl = samplePreview(3),
            artwork = mockArtwork("m3"),
        ),
        Music(
            id = "mock-4",
            title = "Like A Virgin",
            artist = "Madonna",
            songUrl = samplePreview(4),
            artwork = mockArtwork("m4"),
        ),
        Music(
            id = "mock-5",
            title = "Get Lucky",
            artist = "Daft Punk feat. Pharrell Williams",
            songUrl = samplePreview(5),
            artwork = mockArtwork("m5"),
        ),
        Music(
            id = "mock-6",
            title = "Song title here",
            artist = "Artist name here",
            songUrl = samplePreview(6),
            artwork = mockArtwork("m6"),
        )
    )
}
