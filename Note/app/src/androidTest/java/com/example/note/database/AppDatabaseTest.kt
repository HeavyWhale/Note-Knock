package com.example.note.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.note.database.dao.BaseDao
import com.example.note.database.dao.FolderDao
import com.example.note.database.dao.NoteDao
import com.example.note.database.dao.ReminderDao
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Note
import com.example.note.database.entities.Reminder
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class AppDatabaseTest {

    private lateinit var noteDao: NoteDao
    private lateinit var folderDao: FolderDao
    private lateinit var reminderDao: ReminderDao

    private fun removeAllNotes() {
        val folderNum = Model.debugGetFolderCounts()
        for (i in 1..folderNum) {
            Model.deleteNotesByFolderID(i)
        }
    }

    @Before
    fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        AppDatabase.initInstance(context)
        noteDao = AppDatabase.INSTANCE?.getNoteDao()!!
        folderDao = AppDatabase.INSTANCE?.getFolderDao()!!
        reminderDao = AppDatabase.INSTANCE?.getReminderDao()!!

        // Clean up the database
        removeAllNotes()
    }

    @After
    fun closeDb() {
         AppDatabase.destroyInstance()
    }

    // Stolen from: https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ' '
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun genNote(
        id: Int = 0,
        title: String = getRandomString(15),
        body: String = getRandomString(100),
        createDate: Long = System.currentTimeMillis(),
        modifyDate: Long = System.currentTimeMillis()
    ) : Note = Note(id, title, body, createDate, modifyDate)

    private fun genFolder(
        id: Int = 0,
        name: String = getRandomString(15)
    ) : Folder = Folder(id, name)

    @Test
    fun addNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        val folder2Count = noteDao.getNotesCountByFolderID(2)
        val note1 = genNote()
        Model.insertNote(note1.title, note1.body, note1.createTime, "", 2, true)

        assertEquals(folder2Count+1, Model.getNotesCountByFolderID(2))

        val note2 = genNote()
        Model.insertNote(note2.title, note2.body, note2.createTime, "", 2, true)

        assertEquals(folder2Count+2, Model.getNotesCountByFolderID(2))

        val folder3Count = noteDao.getNotesCountByFolderID(3)
        for (i in 1..100) {
            val testNote = genNote()
            Model.insertNote(testNote.title, testNote.body, testNote.createTime, "", 3, true)
        }
        assertEquals(folder3Count+100, Model.getNotesCountByFolderID(3))

        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun deleteNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        val folder2Count = noteDao.getNotesCountByFolderID(2)
        // Add one test note
        val note1 = genNote()
        val currID = Model.insertNote(note1.title, note1.body, note1.createTime, "", 2, true)

        assertEquals(folder2Count+1, Model.getNotesCountByFolderID(2))

        // Test delete one note
        Model.deleteNote(currID, true)

        assertEquals(folder2Count, Model.getNotesCountByFolderID(2))

        // Add massive test notes
        val folder3Count = noteDao.getNotesCountByFolderID(3)
        for (i in 1..100) {
            val testNote = genNote()
            Model.insertNote(testNote.title, testNote.body, testNote.createTime, "", 3, true)
        }

        assertEquals(folder3Count+100, Model.getNotesCountByFolderID(3))

        for (id in (currID+1)..(currID+100)) {
            Model.deleteNote(id, true)
        }
        assertEquals(folder3Count, Model.getNotesCountByFolderID(3))
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun updateNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
//        removeAllNotes()
        val note1 = genNote()
        val currID = Model.insertNote(note1.title, note1.body, note1.createTime, "", 2, true)

        Model.updateNote(currID, "test title", "test body", "")

        // Check if note content has been updated
        assertEquals("test title", Model.debugGetNoteTitleByID(currID))
        assertEquals("test body", Model.debugGetNoteBodyTitleByID(currID))

        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun addFolderTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addFolderTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
//        removeAllNotes()
        val folderNum = Model.debugGetFolderCounts()
        val folder1 = genFolder()
        Model.insertFolder(folder1.name, true)

        assertEquals(folderNum+1, Model.debugGetFolderCounts())

        val folder2 = genFolder()
        Model.insertFolder(folder2.name, true)

        assertEquals(folderNum+2, Model.debugGetFolderCounts())

        for (i in 1..100) {
            val testFolder = genFolder()
            Model.insertFolder(testFolder.name, true)
        }
        assertEquals(folderNum+102, Model.debugGetFolderCounts())

        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addFolderTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun deleteFolderTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteFolderTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        val folderNum = Model.debugGetFolderCounts()

        // Add one test folder
        val folder1 = genFolder()
        val currID = Model.insertFolder(folder1.name, true)

        assertEquals(folderNum+1, Model.debugGetFolderCounts())

        // Test delete one note
        Model.deleteFolder(currID, true)

        assertEquals(folderNum, Model.debugGetFolderCounts())

        // Add massive test notes
        for (i in 1..100) {
            val testFolder = genFolder()
            Model.insertFolder(testFolder.name, true)
        }

        assertEquals(folderNum+100, Model.debugGetFolderCounts())

        for (id in currID+1..currID+100) {
            Model.deleteFolder(id, true)
        }

        assertEquals(folderNum, Model.debugGetFolderCounts())
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteFolderTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun updateFolderTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateFolderTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        val folder1 = genFolder()
        val currID = Model.insertFolder(folder1.name, true)

        Model.updateFolder(currID, "test name", true)

        // Check if note content has been updated
        assertEquals("test name", Model.getFolderNameByID(currID))
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateFolderTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }
}