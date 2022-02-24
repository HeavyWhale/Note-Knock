package com.example.note.database

data class Note (
    val id: Int?,
    var title: String = "",
    var body: String = "",
    var createDate: String = "",
    var modifyDate: String = ""
) {
    // Stolen from: https://stackoverflow.com/a/37524561/9438200
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Note
        return (title == other.title) && (body == other.body)
            && (createDate == other.createDate) && (modifyDate == other.modifyDate)
    }
}