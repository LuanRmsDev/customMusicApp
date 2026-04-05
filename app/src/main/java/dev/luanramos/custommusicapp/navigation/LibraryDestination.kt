package dev.luanramos.custommusicapp.navigation

sealed class LibraryDestination {
    data object LibraryScreen : LibraryDestination()
    data object LibraryPlayerScreen : LibraryDestination()
    data object AlbumDisplayScreen : LibraryDestination()
}
