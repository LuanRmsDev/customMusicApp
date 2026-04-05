package dev.luanramos.custommusicapp.data.mock

import dev.luanramos.custommusicapp.domain.Music

object LibraryMockedData {

    private fun samplePreview(index: Int): String =
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$index.mp3"

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
            songUrl = samplePreview(10)
        ),
        Music(
            id = "album-mock-2",
            title = "Aerodynamic",
            artist = "Daft Punk",
            songUrl = samplePreview(11)
        ),
        Music(
            id = "album-mock-3",
            title = "Harder, Better, Faster, Stronger",
            artist = "Daft Punk",
            songUrl = samplePreview(12)
        ),
        Music(
            id = "album-mock-4",
            title = "Get Lucky",
            artist = "Daft Punk feat. Pharrell Williams",
            songUrl = samplePreview(5)
        ),
        Music(
            id = "album-mock-5",
            title = "Digital Love",
            artist = "Daft Punk",
            songUrl = samplePreview(13)
        ),
        Music(
            id = "album-mock-6",
            title = "One More Time",
            artist = "Daft Punk",
            songUrl = samplePreview(14)
        )
    )

    val songs: List<Music> = listOf(
        Music(
            id = "mock-1",
            title = "Purple Rain",
            artist = "Prince",
            songUrl = samplePreview(1)
        ),
        Music(
            id = "mock-2",
            title = "Power Of Equality",
            artist = "Red Hot Chili Peppers",
            songUrl = samplePreview(2)
        ),
        Music(
            id = "mock-3",
            title = "Something",
            artist = "The Beatles",
            songUrl = samplePreview(3)
        ),
        Music(
            id = "mock-4",
            title = "Like A Virgin",
            artist = "Madonna",
            songUrl = samplePreview(4)
        ),
        Music(
            id = "mock-5",
            title = "Get Lucky",
            artist = "Daft Punk feat. Pharrell Williams",
            songUrl = samplePreview(5)
        ),
        Music(
            id = "mock-6",
            title = "Song title here",
            artist = "Artist name here",
            songUrl = samplePreview(6)
        )
    )
}
