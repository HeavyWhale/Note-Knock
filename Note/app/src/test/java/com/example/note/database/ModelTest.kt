package com.example.note.database

import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.*

internal class ModelTest {

    private fun genNote(title: String, body: String, createDate: String, modifyDate: String, id: Long? = null): Note {
        return Note(id, title, body, createDate, modifyDate)
    }

    private var counter: Long = 0
    private var i = 0
        get() {
            counter++
            return field
        }

    private val noteSize = (Model::getNoteSize)()
    @Test
    fun monolithicTests() {
    // addNote(), noteSize
        assert(i == noteSize) // ????? WTF

        (Model::addNote)(genNote("Title 0", "Body 0", "First", "First"))
        assert(++i == noteSize)
        (Model::addNote)(genNote("Title 1", "Body 1", "Second", "Second"))
        assert(++i == noteSize)
        (Model::addNote)(genNote("Title 2", "Body 2", "Third", "Third"))
        assert(++i == noteSize)

        (Model::addNote)(genNote("A very very very very very very very very very very long title",
            "A very very very very very very very very very very long body",
            "12345678901234567890123456789012345678901234567890",
            "09876543210987654321098765432109876543210987654321"))
        assert(++i == noteSize)

        (Model::printNotes)(-1)


    // deleteNote(), updateNote()
        (Model.deleteNote(1))
        assert(--i == noteSize)

        (Model.deleteNote(5))
        assert(i == noteSize) // size does not change for invalid id

        (Model.updateNote(genNote("fake title", "fake body", "???", "???", -1)))
        (Model.updateNote(genNote("A short title", "A short body", "2022-02-17", "2022-02-17", 3)))
        assert(i == noteSize) // size does not change after update
        (Model::printNotes)(-1)

        // delete all remaining notes

        (Model.deleteNote(0))
        assert(--i == noteSize)

        (Model.deleteNote(3))
        assert(--i == noteSize)

        (Model.deleteNote(2))
        assert(--i == noteSize)

        (Model::printNotes)(-1)

    // massive notes
        for (i in 1..1000) {
            (Model.addNote(genNote("Title $counter", "Body ${counter++}",
                SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date()),
                SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date()))))
        }
        assert(1000 == noteSize)
        Model.printNotes(10)
    }
}