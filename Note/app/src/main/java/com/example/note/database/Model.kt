package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter

object Model {
    private val allNotes = mutableListOf<Note>()

    private var _counter: Long = 0;
    private fun genID(): Long {
        return _counter++;
    }

    fun addNote(newNote: Note) {
        allNotes.add(0, newNote.copy(id = genID()));
        Log.d("Model log", "Added note ID=$_counter");
        // update NotesAdapter
        (NotesAdapter::notifyItemInserted)(0)
    }

    fun getNotes(): MutableList<Note> {
        return allNotes;
    }

    fun getSize(): Int {
        return allNotes.size;
    }
}