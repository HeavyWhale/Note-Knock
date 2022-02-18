package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter
import java.text.SimpleDateFormat
import java.util.*

object Model {
    /* Initialize folders:
        0: All Notes
        1: Snippets
        2: Shopping List
        3: Reminders
     */
    private val folders = mutableListOf<Folder>(
        Folder(0, "All Notes"),
        Folder(1, "Snippets"),
        Folder(2, "Shopping List"),
        Folder(3, "Reminders"))

    private var currentFolder = folders[0]

    private var _counter: Long = 0
    private fun genID(): Long {
        return _counter++
    }

    private var _folderCounter: Long = 1;
    private fun genFolderID(): Long {
        return _folderCounter++
    }

    fun addNote(note: Note) {
        val newNote = note.copy(id = genID())
        folders[0].addNote(newNote)
        // add note to current folder
        if (currentFolder == folders[0]) {
            folders[1].addNote(newNote)
        } else {
            currentFolder.addNote(newNote)
        }
        Log.d("Model log", "Added note ID=$_counter")
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
        return currentFolder.getNotes()
    }

    fun getNoteSize(): Int {
        return currentFolder.getNotesSize();
    }

    fun getFolderName(): String {
        return currentFolder.getName();
    }

    fun addFolder(name: String) {
        val newFolder = Folder(id=genFolderID(), name=name);
        folders.add(newFolder)
        Log.d("Model log", "Added folder ID=$_folderCounter name=$name")
    }

    fun getCurrFolder(): Folder {
        return currentFolder
    }

    fun getFolders(): MutableList<Folder> {
        return folders
    }

    fun getFolderSize(): Int {
        return folders.size;
    }

    fun switchCurrFolder(folderClickedPosition: Int): String {
        currentFolder = folders[folderClickedPosition]
        val currentFolderName = currentFolder.getName()
        Log.d("Model log", "Switched to folder $currentFolderName at position $folderClickedPosition")
        return currentFolderName
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