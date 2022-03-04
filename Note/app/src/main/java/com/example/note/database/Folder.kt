package com.example.note.database

import android.util.Log
import com.example.note.entities.Note

class Folder(val id: Int, var name: String) : java.io.Serializable {

    /*****************************************************************************
     * Properties
     ****************************************************************************/

    // Container for all notes
    var notes = mutableListOf<Note>()
        private set

    /*****************************************************************************
     * Private Functions
     ****************************************************************************/

    private fun findNotePosByID(id: Int?) : Int {
        // https://discuss.kotlinlang.org/t/returning-an-index-of-a-collection-based-on-criteria/9691
        return notes.indexOfFirst { it.id == id }.also {
            if (it == -1) Log.d("ERROR: Folder::findNotePosByID", "Note with ID = $id not found in folder: $this")
        }
    }

    /*****************************************************************************
     * Public Functions
     ****************************************************************************/

    fun addNote(newNote: Note) {
        notes.add(0, newNote)
        Log.d("INFO: Folder::addNote", "Added note: $newNote to folder: $name")
    }

    fun deleteNote(id: Int) {
        findNotePosByID(id).let { pos ->
            if (pos != -1) {
                val removed = notes.removeAt(pos)
                Log.d("INFO: Folder::deleteNote", "Deleted note: $removed")
            }
        }
        // TODO: rearrange displayed notes in main view after deletion
    }

    fun updateNote(updatedNote: Note) {
        // OMG writing tests really helped
        // val pos = findNotePosByID(id)
        val pos = findNotePosByID(updatedNote.id)

        if (pos == -1) return

        notes[pos].run {
            Log.d("INFO: Model::updateNote", "Updated previous note $this to ...")
            title = updatedNote.title
            body = updatedNote.body
            modifyDate = updatedNote.modifyDate
            Log.d("INFO: Model::updateNote", "... current note $this")
        }
        // TODO: update the displayed note in UI
    }

    // For debugging
    fun printNotes(firstN: Int) {
        print("List size: ${notes.size} notes")
        if (firstN == notes.size) {
            println(" (printing all notes)")
        } else {
            println(" (printing first $firstN notes)")
        }
        for ( note in notes.take( firstN ) ) {
            println("    $note")
        }
        println()
    }
}