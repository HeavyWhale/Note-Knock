package com.example.noteserver.repository

import com.example.noteserver.model.Note
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface NoteRepository : CrudRepository<Note, Int> {

    fun findByOrderByModifyTimeAsc(): List<Note>

    fun findByFolderID(folderID: Int): List<Note>

    @Transactional
    fun deleteNotesByFolderID(folderID: Int)
}

@Service
class NoteService(val db: NoteRepository) {

    fun getAllNotes(): List<Note> = db.findByOrderByModifyTimeAsc()

    fun insertNote(note: Note) = db.save(note)

    fun removeNoteByID(noteID: Int) {
        val note = db.findById(noteID)
        if (note.isPresent) {
            db.deleteById(noteID)
        }
    }

    fun removeNotesByFolderID(folderID: Int) {
        db.deleteNotesByFolderID(folderID)
    }

    fun removeAllNotes() {
        db.deleteAll()
    }

    fun updateNoteByID(noteID: Int, note: Note) {
        removeNoteByID(noteID)
        db.save(note)
    }

    fun findNoteByID(noteID: Int): Optional<Note> {
        return db.findById(noteID)
    }

    fun findNotesByFolderID(folderID: Int): List<Note> {
        return db.findByFolderID(folderID)
    }
}
