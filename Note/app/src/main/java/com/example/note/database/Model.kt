package com.example.note.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Note
import com.example.note.database.entities.Reminder
import com.example.note.getCurrentTime

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

    init {
        addDefaultFolders()
    }

    /*****************************************************************************
     * Properties
     ****************************************************************************/

    // Container for all folders
//    val folders: MutableList<Folder> = DF
//        .values()
//        .map { Folder( it.id, it.printableName ) }
//        .toMutableList()
//
//    // pointer to current folder
//    var curFolderID = 0
//        set(value) {
//            field = value
//            Log.d("INFO", "Model::curFolderID - Switched to folder $curFolder at position $value")
//        }

    /**************** Aliases ****************/
    private val noteDao   get() = AppDatabase.INSTANCE?.getNoteDao()!!
    private val folderDao get() = AppDatabase.INSTANCE?.getFolderDao()!!
    private val reminderDao get() = AppDatabase.INSTANCE?.getReminderDao()!!

//    val curFolder get() = folderDao.getFolderNameByID(curFolderID)
//    val folders   get() = folderDao.getAll()
//    val notes     get() = noteDao.getNotesByFolderID(curFolderID)

//    var curNotePosition : Int = -1


    /*****************************************************************************
     * Public Functions
     ****************************************************************************/

    /**************** Reminders ****************/
    fun insertReminder(reminderID: Int = 0, body: String, time: String, noteID: Int) {
        Reminder(reminderID, body, time, noteID).let {
            reminderDao.insert(it)
        }
    }

    fun deleteReminder(reminderID: Int) {
        reminderDao.delete(Reminder(id = reminderID))
    }

    fun updateReminder(reminderID: Int, body: String, time: String, noteID: Int, reminderOff: Boolean) {
        val previousReminder = reminderDao.getReminderByID(reminderID)
        Reminder(reminderID, body, time,
            noteID = if (noteID != -1) previousReminder.noteID else noteID,
            reminderOff
        ).let {
            reminderDao.update(it)
        }
    }

    fun getReminderByID(reminderID: Int): Reminder {
        return reminderDao.getReminderByID(reminderID)
    }

    fun getRemindersByNoteID(noteID: Int): LiveData<List<Reminder>> {
        return reminderDao.getRemindersByNoteID(noteID)
    }

    /**************** Notes ****************/
    fun insertNote(noteID: Int = 0, title: String, body: String, folderID: Int) {
        Log.d("Model", "Insert/update note ID $noteID to folder $folderID")
        Note(noteID, title, body, getCurrentTime(), getCurrentTime(), folderID).let {
            noteDao.insert(it)
        }
    }

    // If the id does not exist, do nothing
    fun deleteNote(noteID: Int) {
        noteDao.delete(Note(id = noteID))
    }

    fun updateNote(noteID: Int, title: String, body: String, folderID: Int = -1) {
        val previousNote = noteDao.getNoteByID(noteID)

        Note(noteID, title, body,
            createTime = previousNote.createTime,
            modifyTime = getCurrentTime(),
            folderID = if (folderID != -1) previousNote.folderID else folderID
        ).let { noteDao.update(it) }
    }

    fun getNoteByID(noteID: Int): Note {
        return noteDao.getNoteByID(noteID)
    }

    fun getNotesByFolderID(folderID: Int): LiveData<List<Note>> {
        if (folderID == 1) {  // All Notes folder
            return noteDao.getAllNotes()
        }
        return noteDao.getNotesByFolderID(folderID)
    }

    fun getNotesCountByFolderID(folderID: Int): Int {
        if (folderID == 1) {  // All Notes folder
            return noteDao.getAllNotesCount()
        }
        return noteDao.getNotesCountByFolderID(folderID)
    }

    /**************** Folders ****************/
    fun addFolder(name: String) {
        folderDao.insert(Folder(id = 0, name = name))
    }

    fun deleteFolder(folderID: Int) {
        folderDao.delete(Folder(id = folderID))
    }

    fun updateFolder(folderID: Int, name: String) {
        folderDao.update(Folder(folderID, name))
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAll()
    }

    fun getFolderNameByID(folderID: Int): String {
        return folderDao.getFolderNameByID(folderID)
    }

    /*****************************************************************************
     * Private Functions
     ****************************************************************************/

    private fun addDefaultFolders() {
        val defaultFolders = DF
            .values()
            .map { Folder( 0, it.printableName ) }
            .toTypedArray()
        println("HERE" + folderDao)
        folderDao.insertAll(*defaultFolders)
    }

    /*****************************************************************************
     * FUNCTIONS FOR DEBUGGING PURPOSE ONLY
     ****************************************************************************/
//    fun printNotes(firstN: Int = curFolder.notes.size) {
//        curFolder.printNotes(firstN)
//    }
//
//    fun getNoteCounter():   Int = --noteIDCounter
//    fun getFolderCounter(): Int = --folderIDCounter
//
//    fun reset() {
//        folders.clear()
//        folders.addAll( DF
//            .values()
//            .map { Folder( it.id, it.printableName ) }
//            .toMutableList()
//        )
//
//        curFolderID = 0
//        noteIDCounter = 0
//        curFolderID = 0
//    }
}