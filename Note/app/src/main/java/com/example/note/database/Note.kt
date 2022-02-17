package com.example.note.database

data class Note (
    val id: Long?,
    var title: String = "",
    var body: String = "",
    var createDate: String = "",
    var modifyDate: String = ""
)