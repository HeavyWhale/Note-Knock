package com.example.note.database

import android.util.Log
import com.example.note.adapters.NotesAdapter
import com.example.note.entities.Note
import com.example.note.getCurrentTime
import java.sql.DriverManager
import java.sql.SQLException

object Model {

//    private val dbConn = initDB()

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

//    private fun initDB(): Connection? {
//        var conn: Connection? = null
//        try {
//            val url = "jdbc:sqlite:allnotes.db"
//            conn = DriverManager.getConnection(url)
//            Log.d("Database", "INFO: Connection to SQLite has been established.")
//        } catch (e: SQLException) {
//            Log.d("Database", "ERROR: ${e.message}")
//        }
//
//        // Add tables to db
//        val createNotesTableQuery =
//            "CREATE TABLE Notes (" +
//                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//                    "Title TEXT," +
//                    "Body TEXT," +
//                    "CreateDate TEXT," +
//                    "ModifyDate TEXT," +
//                    "ParentFolder INTEGER PRIMARY KEY)"
//        val createFoldersTableQuery =
//            "CREATE TABLE Folders (" +
//                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//                    "Name TEXT)"
//
//        if (conn != null) {
//            with (conn) {
//                createStatement().execute(createNotesTableQuery)
//                createStatement().execute(createFoldersTableQuery)
//            }
//        }
//
//        // Add default folders to Folders table
//        for (folder in DF.values()) {
//            val cv = ContentValues().put("Name", folder.printableName)
//
//            val result = db.insert(TABLE_NAME,null,cv)
//
//            val insertDefaultFolder =
//                "INSERT INTO Folders(name)" +
//                        "VALUES (${folder.printableName}) "
//            conn!!.createStatement().execute(insertDefaultFolder)
//        }
//
//        return conn
//    }

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

    var curNotePosition : Int = -1

    var notesAdapter : NotesAdapter? = null

    /****************** Private Counters ******************/
    private var noteIDCounter = 0
        get() = field++     // auto-increment on reference

    private var folderIDCounter = folders.size
        get() = field++

    /*****************************************************************************
     * Public Functions
     ****************************************************************************/

    fun addNote(note: Note) {
//        val newNote = note.copy(id = noteIDCounter)
//
//        // Save a copy to all notes
//        folders[DF.ALL_NOTES.id].addNote(newNote)
//
//        // Set target id to "Snippet" folder id if currently in "All Notes" folder
//        val targetID =
//            if (curFolderID == DF.ALL_NOTES.id)
//                DF.SNIPPETS.id
//            else
//                curFolderID
//
//        folders[targetID].addNote(
//            newNote.apply { parentFolder = targetID }
//        )
//
//        Log.d("Model log", "Added note ID=${newNote.id}")
//        notesAdapter?.notifyItemInserted(0)
        NotesDb.getInstance(this).notesDao().insertNote(note))
    }

    // If the id does not exist, do nothing
    fun deleteNote() {
//        curFolder.deleteNote(id)
//        folders[DF.ALL_NOTES.ordinal].deleteNote(id)
        if (curNotePosition != -1) {
            val note = notes[curNotePosition]
            if (curFolderID != DF.ALL_NOTES.id) {
                folders[DF.ALL_NOTES.id].deleteNote(note.id!!)
            } else {
                folders[note.parentFolder].deleteNote(note.id!!)
            }
            notes.removeAt(curNotePosition)
            notesAdapter?.notifyItemRemoved(curNotePosition)
        }
    }

    fun updateNote(title: String, body: String) {
//        curFolder.updateNote(updatedNote)
//        folders[DF.ALL_NOTES.ordinal].updateNote(updatedNote)
        if (curNotePosition == -1 || curNotePosition > notes.size) {
            Log.d("Model log", "ERROR: updating invalid curNotePosition: $curNotePosition")
            return
        }
        val curNote = notes[curNotePosition]
        curNote.title = title
        curNote.body = body
        curNote.modifyDate = getCurrentTime()
        notesAdapter?.notifyItemChanged(curNotePosition)
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