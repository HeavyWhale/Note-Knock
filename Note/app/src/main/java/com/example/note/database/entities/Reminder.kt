package com.example.note.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "reminders")
@Serializable
data class Reminder(

    @PrimaryKey(autoGenerate = true) var id: Int,
    var body : String = "",
    var time : String = "",
    var noteID: Int = -1,
    var reminderOff : Boolean = false
) : BaseEntity()
