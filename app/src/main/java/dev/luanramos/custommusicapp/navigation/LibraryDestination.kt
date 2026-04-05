package dev.luanramos.custommusicapp.navigation

sealed class LibraryDestination {
    data object LibraryScreen : LibraryDestination()
    data object LibraryPlayerScreen : LibraryDestination()
    data object AlbumDisplayScreen : LibraryDestination()
}

internal fun LibraryDestination.toSaveKey(): String =
    when (this) {
        LibraryDestination.LibraryScreen -> "library"
        LibraryDestination.LibraryPlayerScreen -> "player"
        LibraryDestination.AlbumDisplayScreen -> "album"
    }

internal fun String.toLibraryDestination(): LibraryDestination =
    when (this) {
        "library" -> LibraryDestination.LibraryScreen
        "player" -> LibraryDestination.LibraryPlayerScreen
        "album" -> LibraryDestination.AlbumDisplayScreen
        else -> LibraryDestination.LibraryScreen
    }
