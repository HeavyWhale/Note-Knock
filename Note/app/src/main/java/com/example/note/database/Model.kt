package com.example.note.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Note
import com.example.note.database.entities.Reminder
import com.example.note.getCurrentTime
import java.net.*

object Model {

    /*****************************************************************************
     * Private Properties
     ****************************************************************************/

    private const val SERVER_ADDRESS = "http://127.0.0.1:8080"
//    private val server = HttpClient

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

    /**************** Aliases ****************/
    private val noteDao   get() = AppDatabase.INSTANCE?.getNoteDao()!!
    private val folderDao get() = AppDatabase.INSTANCE?.getFolderDao()!!
    private val reminderDao get() = AppDatabase.INSTANCE?.getReminderDao()!!


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

    fun updateTimeByID(reminderID: Int, time: String) {
        val previousReminder = reminderDao.getReminderByID(reminderID)
        Reminder(reminderID,
            previousReminder.body, time,
            previousReminder.noteID,
            previousReminder.reminderOff
        ).let {
            reminderDao.update(it)
        }
    }

    fun updateNoteIDForReminders(noteID: Int) {
        val reminderIDs = reminderDao.getReminderIDsByNoteID(0)
        for (reminderID in reminderIDs) {
            reminderDao.updateNoteIDByReminderID(noteID, reminderID)
        }
    }

    fun deleteRemindersByNoteID(noteID: Int) {
        var reminderIDs = reminderDao.getReminderIDsByNoteID(noteID)
        for (reminderID in reminderIDs) {
            deleteReminder(reminderID)
        }
    }

    /**************** Notes ****************/
    fun insertNote(noteID: Int = 0, title: String, body: String, createTime: Long, folderID: Int) {
        val currentTime = System.currentTimeMillis()
        Log.d("Model", "Insert note ID $noteID to folder $folderID at time $currentTime")
        Note(noteID, title, body, createTime, currentTime, folderID).let {
            noteDao.insert(it)
        }
    }

    // If the id does not exist, do nothing
    fun deleteNote(noteID: Int) {
        noteDao.delete(Note(id = noteID))
    }

    fun updateNote(noteID: Int, title: String, body: String, folderID: Int = -1) {
        val previousNote = noteDao.getNoteByID(noteID)
        Log.d("Model", "Update note ID $noteID at time ${System.currentTimeMillis()}")

        Note(noteID, title, body,
            createTime = previousNote.createTime,
            modifyTime = System.currentTimeMillis(),
            folderID = if (folderID == -1) previousNote.folderID else folderID
        ).let { noteDao.update(it) }
    }

    fun getNoteIDByPosition(position: Int, currentFolderID: Int): Int {
        val notes = if (currentFolderID == 1) noteDao.getAllNoteList() else noteDao.getNoteListByFolderID(currentFolderID)
        return notes[position].id
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
        noteDao.deleteAllNotesFromFolderID(folderID)
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

    fun getFolderIDByPosition(position: Int): Int {
        val folders = folderDao.getFolderList()
        return folders[position].id
    }

    // For testing purposes
    fun getNoteTitleByID(noteID: Int): String {
        return noteDao.getNoteTitleByID(noteID)
    }

    fun getNoteBodyTitleByID(noteID: Int): String {
        return noteDao.getNoteBodyTitleByID(noteID)
    }

    fun getFolderCounts(): Int {
        return folderDao.getFolderCounts()
    }

    /*****************************************************************************
     * Private Functions
     ****************************************************************************/

    private fun addDefaultFolders() {
        val defaultFolders = DF
            .values()
            .map { Folder( 0, it.printableName ) }
            .toTypedArray()
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