package dev.luanramos.custommusicapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "catalog_list_entries",
    primaryKeys = ["listKey", "position"],
    indices = [
        Index(value = ["listKey"]),
    ],
)
data class CatalogListEntryEntity(
    val listKey: String,
    val position: Int,
    val trackId: String,
)
