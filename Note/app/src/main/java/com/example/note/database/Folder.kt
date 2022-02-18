package com.example.note.database

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

data class Folder(private val id: Long, private var name: String) : java.io.Serializable {
    private var notes = mutableListOf<Note>()

    fun getName(): String {
        return name;
    }

    fun addNote(note: Note) {
        notes.add(0, note);
        Log.d("Folder log", "Added note to folder: $name");
    }

    fun deleteNote(id: Long) {
        val targetNote = notes.find { x -> x.id == id }
        if ( targetNote == null ) {
            Log.d("ERROR: Model::deleteNote", "Id $id not found in allNotes")
            return
        }
        notes.remove(targetNote)
        with (targetNote) {
            Log.d("LOG: Model::deleteNote",
                "Deleted note ID=$id, Title=$title, Body=$body, createDate=$createDate, modifyDate=$modifyDate")
        }
        // TODO: rearrange displayed notes in main view after deletion
    }

    fun updateNote(updatedNote: Note) {
        val targetNote = notes.find { x -> x.id == updatedNote.id }
        if ( targetNote == null ) {
            Log.d("ERROR: Model::updateNote", "Id ${updatedNote.id} not found in allNotes")
            return
        }
        with (targetNote) {
            Log.d("LOG: Model::updateNote",
                "Update note ID=$id, Title=$title, Body=$body, createDate=$createDate, modifyDate=$modifyDate")
        }
        targetNote.apply {
            title = updatedNote.title
            body = updatedNote.body
            modifyDate = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        }
        // TODO: update the displayed note in UI
    }

    fun getNotes(): MutableList<Note> {
        return notes;
    }

    fun getNotesSize(): Int {
        return notes.size;
    }

    fun printNotes(firstN: Int = -1) {
        val noteSize = getNotesSize()
        val N = if (firstN == -1) {
            noteSize
        } else {
            firstN
        }
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        println("List size: $noteSize notes")
        for (i in 0 until N) {
            val note = notes[i]
            with (note) {
                println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Note $id")
                println("Title: $title")
                println("Body: $body")
                println("Create Date: $createDate")
                println("Modify Date: $modifyDate")
            }
        }
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        println()
    }
}