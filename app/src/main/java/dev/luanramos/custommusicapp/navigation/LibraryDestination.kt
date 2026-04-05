package dev.luanramos.custommusicapp.navigation

sealed class LibraryDestination {
    data object LibraryScreen : LibraryDestination()
    data object LibraryPlayerScreen : LibraryDestination()
    data object AlbumDisplayScreen : LibraryDestination()
    /** Wear: song list reached from the Music hub (same list UX as library on other form factors). */
    data object WatchSongsList : LibraryDestination()
}

internal fun LibraryDestination.toSaveKey(): String =
    when (this) {
        LibraryDestination.LibraryScreen -> "library"
        LibraryDestination.LibraryPlayerScreen -> "player"
        LibraryDestination.AlbumDisplayScreen -> "album"
        LibraryDestination.WatchSongsList -> "watch_songs"
    }

internal fun String.toLibraryDestination(): LibraryDestination =
    when (this) {
        "library" -> LibraryDestination.LibraryScreen
        "player" -> LibraryDestination.LibraryPlayerScreen
        "album" -> LibraryDestination.AlbumDisplayScreen
        "watch_songs" -> LibraryDestination.WatchSongsList
        else -> LibraryDestination.LibraryScreen
    }
