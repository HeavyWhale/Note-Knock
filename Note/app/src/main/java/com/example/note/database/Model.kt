package com.example.note.database

import android.util.Log
import androidx.annotation.Nullable
import com.example.note.adapters.NotesAdapter

object Model {
    private val folders = mutableListOf<Folder>(Folder(1, "Orphaned Notes"))
    private val allNotes = Folder(0, "All Notes")
    private var currentFolder = allNotes

    private var _counter: Long = 0;
    private fun genID(): Long {
        return _counter++;
    }

    private var _folderCounter: Long = 1;
    private fun genFolderID(): Long {
        return _folderCounter++;
    }

    fun addNote(note: Note) {
        val newNote = note.copy(id = genID());
        allNotes.addNote(newNote);
        // add note to current folder
        if (currentFolder == allNotes) {
            folders[0].addNote(newNote);
        } else {
            currentFolder.addNote(newNote)
        };
        Log.d("Model log", "Added note ID=$_counter");
        // update NotesAdapter
        (NotesAdapter::notifyItemInserted)(0)
    }

    fun getNotes(): MutableList<Note> {
        return currentFolder.getNotes();
    }

    fun getNoteSize(): Int {
        return currentFolder.getSize();
    }

    fun getFolderName(): String {
        return currentFolder.getName();
    }

    fun addFolder(name: String) {
        val newFolder = Folder(id=genFolderID(), name=name);
        folders.add(newFolder)
        Log.d("Model log", "Added folder ID=$_folderCounter name=$name");
    }

    fun getFolders(): MutableList<Folder> {
        return folders;
    }

    fun getFolderSize(): Int {
        return folders.size;
    }
}