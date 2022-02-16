package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter

class Folder(id: Long, name: String) {
    private val id: Long? = null
    private var name: String? = null
    private var notes = mutableListOf<Note>()

    fun getName(): String? {
        return name;
    }

    fun addNote(note: Note) {
        notes.add(0, note);
        Log.d("Folder log", "Added note to folder: ${name}");
    }

    fun getNotes(): MutableList<Note> {
        return notes;
    }

    fun getSize(): Int {
        return notes.size;
    }
}