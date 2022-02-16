package com.example.note.database

import android.util.Log

object Model {
    private val allNotes = mutableListOf<Note>()

    private var _counter: Long = 0;
    private fun genID(): Long {
        return _counter++;
    }

    fun addNote(newNote: Note) {
        allNotes.add(newNote.copy(id = genID()));
        Log.d("Model log", "Added note ID=$_counter");
    }

    fun getNotes(): MutableList<Note> {
        return allNotes;
    }
}