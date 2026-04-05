package dev.luanramos.custommusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList

internal val libraryBackStackSaver =
    listSaver<SnapshotStateList<LibraryDestination>, String>(
        save = { stack -> stack.map { it.toSaveKey() } },
        restore = { keys ->
            if (keys.isEmpty()) {
                mutableStateListOf(LibraryDestination.LibraryScreen)
            } else {
                mutableStateListOf<LibraryDestination>().apply {
                    addAll(keys.map { it.toLibraryDestination() })
                }
            }
        }
    )

@Composable
fun rememberLibraryBackStack(): SnapshotStateList<LibraryDestination> =
    rememberSaveable(saver = libraryBackStackSaver) {
        mutableStateListOf(LibraryDestination.LibraryScreen)
    }
