package com.example.note.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "folders")
@Serializable
data class Folder(

    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String = ""

) : BaseEntity()