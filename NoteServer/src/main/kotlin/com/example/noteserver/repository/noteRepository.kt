package com.example.noteserver.repository

import com.example.noteserver.model.Note
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.util.*

interface NoteRepository : CrudRepository<Note, Int> {

    fun findByOrderByModifyTimeAsc(): List<Note>

    fun findByFolderID(folderID: Int): List<Note>
}

@Service
class NoteService(val db: NoteRepository) {

    fun getAllNotes(): List<Note> = db.findByOrderByModifyTimeAsc()

    fun insertNote(note: Note) {
        db.save(note)
    }

    fun removeNoteByID(noteID: Int) {
        val note = db.findById(noteID)
        if (note.isPresent) {
            db.deleteById(noteID)
        }
    }

    fun updateNoteById(noteID: Int, note: Note) {
        db.findById(noteID).map { noteDetails ->
            val updatedNote: Note = noteDetails.copy(
                title = note.title,
                body = note.body,
                modifyTime = note.modifyTime,
            )
            db.save(updatedNote)
        }
    }

    fun findNoteById(noteID: Int): Optional<Note> {
        return db.findById(noteID)
    }

    fun findNotesByFolderId(folderID: Int): List<Note> {
        return db.findByFolderID(folderID)
    }
}
