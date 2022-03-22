package com.example.noteserver.model

import javax.persistence.*

@Entity
@Table(name = "REMINDERS")
data class Reminder(
    @Id var id: Int?,
    var body : String = "",
    var time : String = "",
    var noteID: Int = -1,
    var reminderOff : Boolean = false
)