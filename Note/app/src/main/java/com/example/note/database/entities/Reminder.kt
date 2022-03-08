package com.example.note.database.entities

data class Reminder(
    var body : String = "",
    var time : String = "",
    var reminderOn : Boolean = false
)
