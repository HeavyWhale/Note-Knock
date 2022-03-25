package com.example.note.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.*

@Entity(tableName = "notes")
@Serializable
data class Note(
    // autoGenerate: https://developer.android.com/reference/kotlin/androidx/room/PrimaryKey#autogenerate
    @PrimaryKey(autoGenerate = true) var id: Int,

    // @ColumnInfo above each field: not needed, default to construct none-primary fields as ColumnInfo
    var title: String = "",
    var body: String = "",
    var createTime: Long = 0,
    var modifyTime: Long = 0,
    var image: String = "",
    var folderID: Int = -1
) : BaseEntity()

// OMG Room provides search functionality:
// https://developer.android.com/training/data-storage/room/defining-data#search
// https://www.sqlite.org/fts3.html
