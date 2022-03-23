package com.example.note.database.entities

import kotlinx.serialization.*

@Serializable
data class Note(
    var id: Int?,
    var title: String = "",
    var body: String = "",
    var createTime: Long = 0,
    var modifyTime: Long = 0,
    var folderID: Int = -1
)

// OMG Room provides search functionality:
// https://developer.android.com/training/data-storage/room/defining-data#search
// https://www.sqlite.org/fts3.html
