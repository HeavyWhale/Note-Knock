package com.example.note.database

data class Note (
    val id: Long?,
    var title: String = "",
    var body: String = "",
    var date: String = ""
)