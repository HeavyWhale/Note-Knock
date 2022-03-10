package com.example.note.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.note.database.dao.FolderDao
import com.example.note.database.dao.NoteDao
import com.example.note.database.entities.Note
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class AppDatabaseTest {

    private lateinit var noteDao: NoteDao
    private lateinit var folderDao: FolderDao

    @Before
    fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        println("hiiiii")
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        AppDatabase.initInstance(context)
        println("${AppDatabase.INSTANCE?.getNoteDao()}")
        noteDao = AppDatabase.INSTANCE?.getNoteDao()!!
        folderDao = AppDatabase.INSTANCE?.getFolderDao()!!
        println("notedao $noteDao")
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

    @Test
    fun addNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        val note1 = genNote()
        Model.insertNote(note1.id, note1.title, note1.body, note1.createTime, 2)

        assertEquals(1, Model.getNotesCountByFolderID(1))
        assertEquals(1, Model.getNotesCountByFolderID(2))

        val note2 = genNote()
        Model.insertNote(note2.id, note2.title, note2.body, note2.createTime, 3)

        assertEquals(2, Model.getNotesCountByFolderID(1))
        assertEquals(1, Model.getNotesCountByFolderID(3))

        for (i in 1..1000) {
            val testNote = genNote()
            Model.insertNote(testNote.id, testNote.title, testNote.body, testNote.createTime, 3)
        }
        assertEquals(1002, Model.getNotesCountByFolderID(1))
        assertEquals(1001, Model.getNotesCountByFolderID(3))

        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun deleteNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        var currID = 1002
        // Add one test note
        val note1 = genNote()
        Model.insertNote(++currID, note1.title, note1.body, note1.createTime, 2)

        assertEquals(1, Model.getNotesCountByFolderID(1))
        assertEquals(1, Model.getNotesCountByFolderID(2))

        // Test delete one note
        Model.deleteNote(currID)

        assertEquals(0, Model.getNotesCountByFolderID(1))
        assertEquals(0, Model.getNotesCountByFolderID(2))

        // Add massive test notes
        for (i in 1..1000) {
            val testNote = genNote()
            Model.insertNote(++currID, testNote.title, testNote.body, testNote.createTime, 3)
        }

        assertEquals(1000, Model.getNotesCountByFolderID(1))
        assertEquals(1000, Model.getNotesCountByFolderID(3))

        for (id in currID-1000..currID) {
            Model.deleteNote(currID)
        }
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun updateNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        var currID = 2003
        val note1 = genNote()
        Model.insertNote(++currID, note1.title, note1.body, note1.createTime, 2)

        Model.updateNote(currID, "test title", "test body")

        // Check if note content has been updated
        assertEquals("test title", Model.getNoteTitleByID(currID))
        assertEquals("test body", Model.getNoteBodyTitleByID(currID))

        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }
}