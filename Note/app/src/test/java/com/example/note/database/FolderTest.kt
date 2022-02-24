package com.example.note.database

import com.example.note.getCurrentTime
import kotlin.test.*

internal class FolderTest {

    private val testNote = Note(
        1,
        "Test note title",
        "Test note content",
        getCurrentTime()
    )

    @Test
    fun folderNameIsCorrect() {
        val folderID = (Model::getFolderCounter)()
        (Model::addFolder)("testGetName Folder")
        val folders = Model.folders
        assertEquals("testGetName Folder", folders[folderID].name)
    }

    @Test
    fun addNoteToFolder() {
        val allNotesFolder = Model.curFolder
        val oldSize = Model.notes.size
        allNotesFolder.addNote(testNote)
        assertEquals(testNote, Model.notes[0])  // test getNotes()
        assertEquals(testNote, Model.notes[0])  // test getNotes()
        assertEquals(oldSize+1, Model.notes.size)  // test getNotesSize()
    }

    @Test
    fun addMassiveNotesToFolder() {
        val allNotesFolder = Model.folders[Model.DF.ALL_NOTES.id]
        val oldSize = allNotesFolder.notes.size
        for (i in (1..100)) {
            allNotesFolder.addNote( testNote.copy( id = i ) )
        }
        assertEquals(oldSize+100, allNotesFolder.notes.size)
    }
}