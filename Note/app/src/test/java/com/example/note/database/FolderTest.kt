package com.example.note.database

import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.*

internal class FolderTest {

    private val testNote = Note(
        1,
        "Test note title",
        "Test note content",
        SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
    )

    @Test
    fun folderNameIsCorrect() {
        (Model::addFolder)("testGetName Folder")
        val folders = (Model::getFolders)()
        assertEquals("testGetName Folder", folders[1].getName())
    }

    @Test
    fun addNoteToFolder() {
        val allNotesFolder = (Model::getCurrFolder)()
        val oldSize = allNotesFolder.getNotesSize()
        allNotesFolder.addNote(testNote)
        assertEquals(testNote, allNotesFolder.getNotes()[0])  // test getNotes()
        assertEquals(oldSize+1, allNotesFolder.getNotesSize())  // test getNotesSize()
    }

    @Test
    fun addMassiveNotesToFolder() {
        val allNotesFolder = (Model::getCurrFolder)()
        val oldSize = allNotesFolder.getNotesSize()
        for (i in (1..100)) {
            allNotesFolder.addNote(testNote.copy(id=i.toLong()))
        }
        assertEquals(oldSize+100, allNotesFolder.getNotesSize())
    }
}