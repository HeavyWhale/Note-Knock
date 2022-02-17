package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter
import java.text.SimpleDateFormat
import java.util.*

object Model {
    private val allNotes = mutableListOf<Note>()

    private var _counter: Long = 0
//        get() = _counter++
        get() = field++

    val noteSize: Int
        get() = allNotes.size

    fun addNote(newNote: Note) {
        val note = newNote.copy(id = _counter)
//        Log.d("LOG: Model::addNote", "Added note ID=${note.id}");
        allNotes.add(note)
        // update NotesAdapter
//        (NotesAdapter::notifyItemInserted)(0)
    }

    fun deleteNote(id: Long) {
        val targetNote = allNotes.find { x -> x.id == id }
        if ( targetNote == null ) {
//            Log.d("ERROR: Model::deleteNote", "Id $id not found in allNotes")
            return
        }
        allNotes.remove(targetNote)
        with (targetNote) {
//            Log.d("LOG: Model::deleteNote",
//                "Deleted note ID=$id, Title=$title, Body=$body, createDate=$createDate, modifyDate=$modifyDate")
        }
        // TODO: rearrange displayed notes in main view after deletion
    }

    fun updateNote(updatedNote: Note) {
        val targetNote = allNotes.find { x -> x.id == updatedNote.id }
        if ( targetNote == null ) {
//            Log.d("ERROR: Model::updateNote", "Id ${updatedNote.id} not found in allNotes")
            return
        }
        with (targetNote) {
//            Log.d("LOG: Model::updateNote",
//                "Update note ID=$id, Title=$title, Body=$body, createDate=$createDate, modifyDate=$modifyDate")
        }
        targetNote.apply {
            title = updatedNote.title
            body = updatedNote.body
            modifyDate = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        }
        // TODO: update the displayed note in UI
    }

    fun getNotes(): MutableList<Note> {
        return allNotes;
    }

    // for debugging
    fun printNotes(firstN: Int = -1) {
        val N = if (firstN == -1) {
            noteSize
        } else {
            firstN
        }
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        println("List size: $noteSize notes")
        for (i in 0..N-1) {
            val note = allNotes[i]
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