package com.example.note.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.note.database.Model.noteDao
import com.example.note.database.dao.*
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Note
import com.example.note.database.entities.Reminder
import com.example.note.toPrettyString
import com.example.note.toPrettyTime
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Model {

    private var colorCounter = 0;
    private val colors = listOf<String>("#FDBE3B", "#FF4842", "#3A52FC", "#000000")
    val color
        get() = colors[colorCounter++ % 4]


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

        val id: Int = ordinal + 1 // + 1 since auto-generated ID by Room starts with 1
    }
    val DF_LENGTH = DF.values().size

    /*****************************************************************************
     * Private Properties
     ****************************************************************************/

    private const val BASE_URL = "http://10.0.2.2:8080"
    private val daos: List<BaseDao> = listOf(
        AppDatabase.INSTANCE!!.getNoteDao(),
        AppDatabase.INSTANCE!!.getFolderDao(),
        AppDatabase.INSTANCE!!.getReminderDao()
    )

    /**************** Aliases ****************/
    private val noteDao   get() = daos[0] as NoteDao
    private val folderDao get() = daos[1] as FolderDao
    private val reminderDao get() = daos[2] as ReminderDao

    /*****************************************************************************
     * Private Functions
     ****************************************************************************/

//    private fun addDefaultFolders() {
//        val defaultFolders = DF
//            .values()
//            .map { Folder( 0, it.printableName ) }
//            .toTypedArray()
//        folderDao.insertAll(*defaultFolders)
//    }

    private fun updateDaosFromServer() {
        for (dao in daos) {
            dao.pullFromServer(BASE_URL)
        }
    }

    /*****************************************************************************
     * Initialization
     ****************************************************************************/

    init {
        updateDaosFromServer()
    }

    /*****************************************************************************
     * Public Properties
     ****************************************************************************/

    var editedReminder = false

    /*****************************************************************************
     * Public Functions
     ****************************************************************************/

    fun pullDataFromServer() {
        updateDaosFromServer()
    }

    /************************** Notes **************************/
    fun insertNote(
        title: String,
        body: String,
        createTime: Long,
        image: String,
        folderID: Int,
        color: String,
        test: Boolean = false
    ): Int {
        val currentTime = System.currentTimeMillis()
        Log.d("Model", "Inserting new note with title \"$title\", " +
            "body \"$body\" to folder \"$folderID\" " +
            "at createTime \"${currentTime.toPrettyTime()}\"")

        val note = Note(0, title, body, createTime, currentTime, image, folderID, color = color)
        val assignedID = noteDao.insert(note).toInt()

        if (test) return assignedID

        noteDao.pushToServer(
            item = note.copy(id = assignedID),
            operation = BaseDao.OPERATION.INSERT,
            baseURL = BASE_URL
        )
        return assignedID
    }

    // If the id does not exist, do nothing
    fun deleteNote(noteID: Int, test: Boolean = false) {
        Log.d("Model", "Deleting note with id \"$noteID\"")

        deleteRemindersByNoteID(noteID)

        val note = Note(id = noteID)
        noteDao.delete(note)
        if (test) return

        noteDao.pushToServer(
            item = note,
            operation = BaseDao.OPERATION.DELETE,
            baseURL = BASE_URL
        )
    }

    fun deleteNotesByFolderID(folderID: Int, test: Boolean = false) {
        Log.d("Model", "Deleting notes with folderID \"$folderID\". " +
            "Now there are ${noteDao.getAllNotesCount()} notes in its DAO.")

        noteDao.deleteNotesByFolderID(folderID)

        Log.d("Model", "Deleted notes. " +
            "Now there are ${noteDao.getAllNotesCount()} notes in its DAO.")

        if (test) return

        noteDao.pushToServer(
            item = Note(id = 0, folderID = folderID),
            operation = BaseDao.OPERATION.MULTIPLE_DELETE,
            baseURL = BASE_URL
        )
    }

    fun updateNote(noteID: Int, title: String, body: String, image: String, folderID: Int = 0, test: Boolean = false) {
        val previousNote = noteDao.getNoteByID(noteID)
        val newNote = Note(noteID, title, body,
            createTime = previousNote.createTime,
            modifyTime = System.currentTimeMillis(),
            image,
            folderID = if (folderID == 0) previousNote.folderID else folderID
        )
        Log.d("Model", "Updating note:\n" +
            "\tThe original note: $previousNote\n" +
            "\tWith new title \"$title\", new body \"$body\", new folderID \"$folderID\"")

        noteDao.update(newNote)
        Log.d("Model", "Updated note:\n" +
            "\tThe new note: ${noteDao.getNoteByID(noteID)}")

        if (test) return

        noteDao.pushToServer(
            item = newNote,
            operation = BaseDao.OPERATION.UPDATE,
            baseURL = BASE_URL
        )
    }

    fun getNoteByID(noteID: Int): Note = noteDao.getNoteByID(noteID)

    fun getNotesByFolderID(folderID: Int): LiveData<List<Note>> =
        if (folderID == DF.ALL_NOTES.id) {
            noteDao.getAllNotes()
        } else {
            noteDao.getNotesByFolderID(folderID)
        }

    fun getNotesListByFolderID(folderID: Int): List<Note> =
        if (folderID == DF.ALL_NOTES.id) {
            noteDao.getAllNotesList()
        } else {
            noteDao.getNotesListByFolderID(folderID)
        }


    fun getNotesCountByFolderID(folderID: Int): Int =
        if (folderID == DF.ALL_NOTES.id) {
            noteDao.getAllNotesCount()
        } else {
            noteDao.getNotesCountByFolderID(folderID)
        }

    /**************** Folders ****************/
    fun insertFolder(name: String, test: Boolean = false): Int {
        Log.d("Model", "Inserting new folder with name \"$name\"")
        val folder = Folder(id = 0, name = name)

        val assignedID = folderDao.insert(folder).toInt()
        if (test) return assignedID

        folderDao.pushToServer(
            item = folder.copy(id = assignedID),
            operation = BaseDao.OPERATION.INSERT,
            baseURL = BASE_URL
        )
        return assignedID
    }

    fun deleteFolder(folderID: Int, test: Boolean = false) {
        Log.d("Model", "Deleting folder with id \"$folderID\"")

        deleteRemindersByFolderID(folderID)
        deleteNotesByFolderID(folderID)

        val folder = Folder(id = folderID)
        folderDao.delete(folder)
        if (test) return

        folderDao.pushToServer(
            item = folder,
            operation = BaseDao.OPERATION.DELETE,
            baseURL = BASE_URL
        )
    }

    fun updateFolder(folderID: Int, name: String, test: Boolean = false) {
        val folder = Folder(folderID, name)

        Log.d("Model", "Updating folder:\n" +
            "\tThe original folder: ${folderDao.getFolderByID(folderID)}\n" +
            "\tWith new name \"$name\"")
        folderDao.update(folder)
        Log.d("Model", "Updated folder:\n" +
            "\tThe new note: ${folderDao.getFolderByID(folderID)}")

        if (test) return

        folderDao.pushToServer(
            item = folder,
            operation = BaseDao.OPERATION.UPDATE,
            baseURL = BASE_URL
        )
    }

    fun getAllFolders(): LiveData<List<Folder>> = folderDao.getAllFolders()

    fun getFolderNameByID(folderID: Int): String = folderDao.getFolderNameByID(folderID)

    fun getFolderIDByPosition(position: Int): Int = getAllFolders().value!![position].id

    /**************** Reminders ****************/
    fun insertReminder(body: String, time: String, noteID: Int): Int {
        val reminder = Reminder(0, body, time, noteID)
        Log.d("Model", "Inserting new reminder $reminder")

        val assignedID = reminderDao.insert(reminder).toInt()
        reminderDao.pushToServer(
            item = reminder.copy(id = assignedID),
            operation = BaseDao.OPERATION.INSERT,
            baseURL = BASE_URL
        )
        return assignedID
    }

    fun deleteReminder(reminderID: Int) {
        Log.d("Model", "Deleting reminder with id \"$reminderID\"")

        val reminder = Reminder(id = reminderID)
        reminderDao.delete(reminder)
        reminderDao.pushToServer(
            item = reminder,
            operation = BaseDao.OPERATION.DELETE,
            baseURL = BASE_URL
        )
    }

    fun deleteRemindersByNoteID(noteID: Int) {
        Log.d("Model", "Deleting reminders with noteID \"$noteID\". " +
            "Now there are ${reminderDao.getAllRemindersCount()} reminders in its DAO.")

        reminderDao.deleteRemindersByNoteID(noteID)

        Log.d("Model", "Deleted reminders. " +
            "Now there are ${reminderDao.getAllRemindersCount()} reminders in its DAO.")

        reminderDao.pushToServer(
            item = Reminder(id = 0, noteID = noteID),
            operation = BaseDao.OPERATION.MULTIPLE_DELETE,
            baseURL = BASE_URL
        )
    }

    fun deleteRemindersByFolderID(folderID: Int) {
        Log.d("Model", "Deleting reminders with folderID \"$folderID\". " +
            "Now there are ${reminderDao.getAllRemindersCount()} reminders in its DAO.")
        
        getNotesListByFolderID(folderID).map { note ->
            deleteRemindersByNoteID(note.id)
        }

        Log.d("Model", "Deleted reminders. " +
            "Now there are ${reminderDao.getAllRemindersCount()} reminders in its DAO.")

        // No need to push to server, deleteRemindersByNoteID does that for us
    }

    fun updateReminder(
        reminderID: Int,
        body: String,
        time: String,
        noteID: Int,
        reminderOff: Boolean
    ) {
        val previousReminder = reminderDao.getReminderByID(reminderID)
        val newReminder = Reminder(reminderID, body, time, noteID, reminderOff)

        Log.d("Model", "Updating reminder:\n" +
            "\tThe original reminder: $previousReminder\n" +
            "\tWith new body \"$body\", new time \"$time\", " +
            "new noteID \"$noteID\", new reminderOff \"$reminderOff\"")
        reminderDao.update(newReminder)
        Log.d("Model", "Updated reminder:\n" +
            "\tThe new reminder: ${reminderDao.getReminderByID(reminderID)}")

        reminderDao.pushToServer(
            item = newReminder,
            operation = BaseDao.OPERATION.UPDATE,
            baseURL = BASE_URL
        )
    }

    fun updateReminderTimeByID(reminderID: Int, time: String) {
        val prev = reminderDao.getReminderByID(reminderID)
        updateReminder(
            reminderID = reminderID,
            body = prev.body,
            time = time,
            noteID = prev.noteID,
            reminderOff = prev.reminderOff
        )
    }

    // FIXME: ??? No idea what this function wants to achieve
    fun updateNoteIDForReminders(noteID: Int) {
        val reminderIDs = reminderDao.getReminderIDsByNoteID(0)
        for (reminderID in reminderIDs) {
            reminderDao.updateNoteIDByReminderID(noteID, reminderID)
        }
    }

    fun updateRemindersNoteIDByNoteID(oldNoteID: Int, newNoteID: Int) {
        Log.d("Model", "Updating reminders with old noteID = \"$oldNoteID\" to " +
            "new noteID = \"$newNoteID\"")

        reminderDao.updateRemindersNoteIDByNoteID(oldNoteID, newNoteID)
        val ENDPOINT = "$BASE_URL/reminders?oldNoteID=$oldNoteID&newNoteID=$newNoteID".also {
            Log.d("Model", "Pushing to endpoint \"$it\"")
        }
        CoroutineScope(Dispatchers.IO).launch {
            HttpClient().put(ENDPOINT)
        }
        Log.d("Model", "Successfully pushed updateRemindersNoteIDByNoteID to server")
    }

    fun getReminderByID(reminderID: Int) = reminderDao.getReminderByID(reminderID)

    fun getRemindersByNoteID(noteID: Int): LiveData<List<Reminder>> =
        reminderDao.getRemindersByNoteID(noteID)

    /*****************************************************************************
     * FUNCTIONS FOR DEBUGGING PURPOSE ONLY
     ****************************************************************************/
    fun debugGetNoteTitleByID(noteID: Int): String {
        return noteDao.getNoteTitleByID(noteID)
    }

    fun debugGetNoteBodyTitleByID(noteID: Int): String {
        return noteDao.getNoteBodyTitleByID(noteID)
    }

    fun debugGetFolderCounts(): Int {
        return folderDao.getFolderCounts()
    }
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