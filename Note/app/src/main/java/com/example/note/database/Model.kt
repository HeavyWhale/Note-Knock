package com.example.note.database

import android.util.Log

class Model {
    private val allNotes = mutableListOf<Note>()

    private var _counter: Long = 0;
    private fun genID(): Long {
        return _counter++;
    }

    fun addNote(newNote: Note) {
        allNotes.add(newNote.copy(id = genID()));
        Log.d("NOTE_1234", "Added note ID=$_counter");
    }

    fun getNotes(): MutableList<Note> {
        return allNotes;
    }
}