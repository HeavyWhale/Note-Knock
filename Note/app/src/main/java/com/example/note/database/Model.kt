package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter

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
        // update NotesAdapter
        (NotesAdapter::notifyItemInserted)(0)
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

    fun switchCurrFolder(folderClickedPosition: Int) {
        currentFolder = folders[folderClickedPosition]
        val currentFolderName = currentFolder.getName()
        Log.d("Model log", "Switched to folder $currentFolderName at position $folderClickedPosition")
    }
}