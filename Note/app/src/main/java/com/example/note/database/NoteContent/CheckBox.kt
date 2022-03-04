package com.example.note.database.NoteContent

class CheckBox : NoteContent {
    public override var content = ""
    private var checked : Boolean = false
    public var notificationOn : Boolean = false

    fun onClick() {
        checked = !checked
    }
}