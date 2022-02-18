package com.example.note.database

import android.util.Log

data class Folder(private val id: Long, private var name: String) : java.io.Serializable {
    private var notes = mutableListOf<Note>()

    fun getName(): String {
        return name;
    }

    fun addNote(note: Note) {
        notes.add(0, note);
        Log.d("Folder log", "Added note to folder: $name");
    }

    fun getNotes(): MutableList<Note> {
        return notes;
    }

    fun getNotesSize(): Int {
        return notes.size;
    }
}