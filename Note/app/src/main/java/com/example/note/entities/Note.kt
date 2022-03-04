package com.example.note.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "body") var body: String,
    @ColumnInfo(name = "createTime") var createTime: String,
    @ColumnInfo(name = "modifyTime") var modifyTime: String,
    @ColumnInfo(name = "folderID") var folderID: Int
) : java.io.Serializable



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