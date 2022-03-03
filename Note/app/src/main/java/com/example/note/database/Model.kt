package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter

object Model {

    /*****************************************************************************
     * Enum subclass for setting up default folders
     ****************************************************************************/

    // Usage:
    //      val folder = DefaultFolders.SHOPPING_LIST
    //      println("The folder \"${folder.printableName}\" has id ${folder.id}" )
    // https://blog.logrocket.com/kotlin-enum-classes-complete-guide/
    enum class DF(val printableName: String) { // DF == "Default Folders"
        ALL_NOTES       ("All Notes"),
        SNIPPETS        ("Snippets"),
        SHOPPING_LIST   ("Shopping List"),
        REMINDERS       ("Reminders");

        val id: Int = ordinal
    }

    /*****************************************************************************
     * Properties
     ****************************************************************************/

    // Container for all folders
    val folders: MutableList<Folder> = DF
        .values()
        .map { Folder( it.id, it.printableName ) }
        .toMutableList()

    // pointer to current folder
    var curFolderID = 0
        set(value) {
            field = value
            Log.d("INFO: Model::curFolderID", "Switched to folder $curFolder at position $value")
        }

    /**************** Aliases ****************/
    val curFolder get() = folders[curFolderID]
    // Mimic class Folder's properties, pointed by curFolder
    val notes get() = curFolder.notes
    val id    get() = curFolder.id
    val name  get() = curFolder.name

    /****************** Private Counters ******************/
    private var noteIDCounter = 0
        get() = field++     // auto-increment on reference

    private var folderIDCounter = folders.size
        get() = field++

    /*****************************************************************************
     * Public Functions
     ****************************************************************************/

    fun addNote(note: Note) {
        val newNote = note.copy(id = noteIDCounter)

        // Save a copy to all notes
        folders[DF.ALL_NOTES.id].addNote(newNote)

        // Set target id to "Snippet" folder id if currently in "All Notes" folder
        val targetID =
            if (curFolderID == DF.ALL_NOTES.id)
                DF.SNIPPETS.id
            else
                curFolderID

        folders[targetID].addNote(newNote)
        Log.d("Model log", "Added note ID=${newNote.id}")
        NotesAdapter.notifyItemInserted(0)
    }

    // If the id does not exist, do nothing
    fun deleteNote(id: Int) {
        curFolder.deleteNote(id)
        folders[DF.ALL_NOTES.ordinal].deleteNote(id)
    }

    fun updateNote(updatedNote: Note) {
        curFolder.updateNote(updatedNote)
        folders[DF.ALL_NOTES.ordinal].updateNote(updatedNote)
    }
    fun addFolder(name: String) {
        folders.add( Folder( folderIDCounter, name ) )
        Log.d("Model log", "Added folder ID=${folders.last().id} name=$name")
    }

    /*****************************************************************************
     * FUNCTIONS FOR DEBUGGING PURPOSE ONLY
     ****************************************************************************/
    fun printNotes(firstN: Int = curFolder.notes.size) {
        curFolder.printNotes(firstN)
    }

    fun getNoteCounter():   Int = --noteIDCounter
    fun getFolderCounter(): Int = --folderIDCounter

    fun reset() {
        folders.clear()
        folders.addAll( DF
            .values()
            .map { Folder( it.id, it.printableName ) }
            .toMutableList()
        )

        curFolderID = 0
        noteIDCounter = 0
        curFolderID = 0
    }
}