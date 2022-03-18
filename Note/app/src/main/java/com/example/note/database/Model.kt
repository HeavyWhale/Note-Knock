package com.example.note.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Note
import com.example.note.database.entities.Reminder
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.time.Duration

object Model {

    /*****************************************************************************
     * Private Properties
     ****************************************************************************/

    private val baseURL = "http://127.0.0.1:8080"

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
    fun insertNote(title: String, body: String, createTime: Long, folderID: Int) {
        val currentTime = System.currentTimeMillis()
        val jsonNote = Json.encodeToString(Note(0, title, body, createTime, currentTime, folderID))
        val url = URL("$baseURL/notes")

        if (pushDataToHttpServer("POST", jsonNote, url)) {
            Log.d("Model", "Insert note $title to folder $folderID at time $currentTime")
        } else {
            Log.e("Model", "Insert note $title failed")
        }
    }

    // If the id does not exist, do nothing
    fun deleteNote(noteID: Int) {
        val url = URL("$baseURL/notes/$noteID")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "DELETE"

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("Model", "Failed to delete note $noteID")
            }
        }
    }

    fun updateNote(noteID: Int, title: String, body: String, folderID: Int = -1) {
        val note = Note(
            noteID, title, body,
            createTime = 0,
            modifyTime = System.currentTimeMillis(),
            folderID = 0
        )
        val jsonNote = Json.encodeToString(note)
        val url = URL("$baseURL/notes/$noteID")

        if (pushDataToHttpServer("PUT", jsonNote, url)) {
            Log.d("Model", "Update note $noteID")
        } else {
            Log.e("Model", "Update note $noteID failed")
        }
    }

    fun getAllNotes(): List<Note> {
        val url = URL("$baseURL/notes")
        return getListDataFromHttpServer(url)
    }

    fun getNoteIDByPosition(position: Int, currentFolderID: Int): Int {
        val notes = if (currentFolderID == 1) getAllNotes() else getNoteListByFolderID(currentFolderID)
        return notes[position].id
    }

    fun getNoteByID(noteID: Int): Note? {
        val url = URL("$baseURL/notes/$noteID")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            doInput = true
            setRequestProperty("Accept", "application/json")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = inputStream.bufferedReader().use {
                    it.readText()
                }  // defaults to UTF-8

                return Json.decodeFromString(response)
            } else {
                Log.e("Model", "Getting all notes failed")
                return null
            }
        }
    }

    fun getNotesByFolderID(folderID: Int): LiveData<List<Note>> {
        if (folderID == 1) {  // All Notes folder
            // TODO: getAllNotes now returns list<note>, need livedata instead
            val url = URL("$baseURL/notes")
            return getListDataFromHttpServer(url)
        }
        val url = URL("$baseURL/notes/$folderID")
        return getListDataFromHttpServer(url)
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

    private fun pushDataToHttpServer(request: String, jsonData: String, url: URL): Boolean {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = request
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")

            // Write to OutputStream
            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(jsonData)
            outputStreamWriter.flush()

            return responseCode == HttpURLConnection.HTTP_OK
        }
    }

    private fun getListDataFromHttpServer(url: URL): List<Note> {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            doInput = true
            setRequestProperty("Accept", "application/json")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = inputStream.bufferedReader().use {
                    it.readText()
                }  // defaults to UTF-8

                return Json.decodeFromString(response)
            } else {
                Log.e("Model", "Getting all notes failed")
                return emptyList()
            }
        }
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