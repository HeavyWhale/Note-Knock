package com.example.note.database

import com.example.note.entities.Note
import com.example.note.getCurrentTime
import kotlin.random.Random
import kotlin.test.*

internal class ModelTest {

    private var noteIDCounter = -1
    private var notesSizeCounter = 0

    @BeforeTest @AfterTest
    fun reset() {
        noteIDCounter = -1
        notesSizeCounter = 0
        Model.reset()
    }

    // Stolen from: https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ' '
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun genNote(
        id: Int? = Random.nextInt(),
        title: String = getRandomString(15),
        body: String = getRandomString(100),
        createDate: String = getRandomString(5),
        modifyDate: String = getCurrentTime()
    ) : Note = Note(id, title, body, createDate, modifyDate)

    private fun addNoteAndTest(note: Note) {
        note.let {
            Model.addNote(it)
            noteIDCounter++
            notesSizeCounter++

            assertEquals(noteIDCounter, Model.notes.first().id)     // new id appended to new note
            assertEquals(noteIDCounter, Model.getNoteCounter())     // Model's counter updated
            assertEquals(Model.notes.first(), it)                   // new note is the first
            assertEquals(notesSizeCounter, Model.notes.size)        // size added 1
        }
    }

    private fun deleteNoteAndTest(id: Int, exists: Boolean) {
        Model.deleteNote(id)
        if (exists) notesSizeCounter--

        assertEquals(noteIDCounter, Model.getNoteCounter())         // counter does not change
        assertEquals(null, Model.notes.find { it.id == id }) // actually deleted
        assertEquals(notesSizeCounter, Model.notes.size)            // size changed
    }

    private fun updateNoteAndTest(notePosition: Int, exists: Boolean) {
        val before = Model.notes.hashCode()
        Model.curNotePosition = notePosition
        val title = getRandomString(15)
        val body = getRandomString(100)
        Model.updateNote(title, body)
        if (exists) {
            assertNotEquals(before, Model.notes.hashCode())
            val target = Model.notes[notePosition]   // actually updated
            assertEquals(title, target.title)              // only compare title ...
            assertEquals(body, target.body)                // ... and body
            assertEquals(getCurrentTime(), target.modifyDate)    // ... and modifyDate (updateNote should not change createDate)
        }

        assertEquals(noteIDCounter, Model.getNoteCounter())         // counter does not change
        assertEquals(notesSizeCounter, Model.notes.size)            // size does not change
    }

    @Test
    fun addNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        addNoteAndTest(genNote(null, "My Title1", "My Body1", "Created now", "Modified later"))
        addNoteAndTest(genNote(-18739, "!#199`", "BODY__++ 123", "?", ""))
        addNoteAndTest(genNote(
            123456789,
            "A very very very very very very very very very very long title",
            "A very very very very very very very very very very long body",
            "12345678901234567890123456789012345678901234567890",
            "09876543210987654321098765432109876543210987654321"
        ))
        for (i in 1..1000) {
            addNoteAndTest(genNote())
        }
        assertEquals(1003, Model.notes.size)

        Model.printNotes(10)
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @addNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun deleteNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

        // Basic test
        addNoteAndTest(genNote(null, "My Title1", "My Body1", "Created now", "Modified later"))
        deleteNoteAndTest(0, true)
        assertEquals(0, Model.notes.size)

        // Weired parameters
        addNoteAndTest(genNote(-18739, "!#199`", "BODY__++ 123", "?", ""))
        addNoteAndTest(genNote(
            123456789,
            "A very very very very very very very very very very long title",
            "A very very very very very very very very very very long body",
            "12345678901234567890123456789012345678901234567890",
            "09876543210987654321098765432109876543210987654321"
        ))
        deleteNoteAndTest(1, true)
        deleteNoteAndTest(2, true)

        // DNE indices
        deleteNoteAndTest(2, false)
        deleteNoteAndTest(1, false)
        deleteNoteAndTest(0, false)
        deleteNoteAndTest(-2810, false)
        deleteNoteAndTest(187416209, false)

        // Massive
        for (i in 1..1000) {
            addNoteAndTest(genNote())
        }
        val deleteSequence = (3..noteIDCounter).toList().shuffled()
        for (i in deleteSequence) {
            deleteNoteAndTest(i, true)
        }
        Model.printNotes() // should be empty
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @deleteNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    @Test
    fun updateNoteTest() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateNoteTest: START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        // Basic test
        val note = genNote(null, "My Title1", "My Body1", "Created now", "Modified later")
        addNoteAndTest(note)
        updateNoteAndTest(0, true)
        updateNoteAndTest(-1, false)

        addNoteAndTest(genNote(id = noteIDCounter))
        addNoteAndTest(genNote(id = noteIDCounter))
        addNoteAndTest(genNote(id = noteIDCounter))
        addNoteAndTest(genNote(id = noteIDCounter))
        addNoteAndTest(genNote(id = noteIDCounter))  // should have 6 notes by now

        updateNoteAndTest(2, true)
        updateNoteAndTest(5, true)
        updateNoteAndTest(1, true)
        deleteNoteAndTest(0, true)
        deleteNoteAndTest(1, true)
        deleteNoteAndTest(2, true)
        deleteNoteAndTest(3, true)
        deleteNoteAndTest(4, true)
        deleteNoteAndTest(5, true)
        assertEquals(0, Model.notes.size)

        // DNE indices
        updateNoteAndTest(764123, false)
        updateNoteAndTest(-1038, false)

        // Massive
        for (i in 1..1000) {
            addNoteAndTest(genNote())
        }
        val updateSequence = (1..noteIDCounter).toList().shuffled()
        for (i in updateSequence) {
            updateNoteAndTest(i, true)
        }
        for (i in updateSequence.shuffled()) {
            deleteNoteAndTest(i, true)
        }
        assertEquals(0, Model.notes.size)

        Model.printNotes() // should be empty
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> @updateNoteTest: END   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }
}