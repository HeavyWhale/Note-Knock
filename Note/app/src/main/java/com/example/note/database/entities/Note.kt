package com.example.note.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(

    // autoGenerate: https://developer.android.com/reference/kotlin/androidx/room/PrimaryKey#autogenerate
    @PrimaryKey(autoGenerate = true) var id: Int,

    // @ColumnInfo above each field: not needed, default to construct none-primary fields as ColumnInfo
    var title: String = "",
    var body: String = "",
    var createTime: String = "",
    var modifyTime: String = "",
    var folderID: Int = -1

) : java.io.Serializable

// OMG Room provides search functionality:
// https://developer.android.com/training/data-storage/room/defining-data#search
// https://www.sqlite.org/fts3.html


//    val id: Int?,
//    var title: String = "",
//    var body: String = "",
//    var createDate: String = "",
//    var modifyDate: String = "",
//    var parentFolder: Int = -1
//{
//     Stolen from: https://stackoverflow.com/a/37524561/9438200
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other?.javaClass != javaClass) return false
//        other as Note
//        return (title == other.title) && (body == other.body)
//            && (createDate == other.createDate) && (modifyDate == other.modifyDate)
//    }
//}