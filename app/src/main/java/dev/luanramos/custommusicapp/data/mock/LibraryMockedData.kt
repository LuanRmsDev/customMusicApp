package dev.luanramos.custommusicapp.data.mock

import dev.luanramos.custommusicapp.domain.LibrarySong

object LibraryMockedData {
    val songs: List<LibrarySong> = listOf(
        LibrarySong("Purple Rain", "Prince"),
        LibrarySong("Song title here", "Artist name here"),
        LibrarySong("Song title here", "Artist name here"),
        LibrarySong("Song title here", "Artist name here"),
        LibrarySong("Song title here", "Artist name here"),
        LibrarySong("Song title here", "Artist name here")
    )
}