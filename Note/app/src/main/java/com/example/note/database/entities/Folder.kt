package com.example.note.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder(

    @PrimaryKey(autoGenerate = true) var id: Int,

    var name: String = ""
)