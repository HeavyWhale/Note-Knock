package com.example.noteserver.controller

import com.example.noteserver.model.Note
import com.example.noteserver.repository.NoteRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NoteResource(private val noteRepository: NoteRepository) {
    @GetMapping
    fun index(): ResponseEntity<List<Note>> {
        val notes = noteRepository.findAll()
        if (notes.isEmpty()) {
            return ResponseEntity<List<Note>>(HttpStatus.NO_CONTENT) // should be empty list?
        }
        return ResponseEntity<List<Note>>(notes, HttpStatus.OK)
    }

    @PostMapping
    fun post(@RequestBody note: Note) {
        noteRepository.save(note)
    }

    @DeleteMapping("/{id}")
    fun removeNoteById(@PathVariable("id") noteID: Int): ResponseEntity<Void> {
        val note = noteRepository.findById(noteID)
        if (note.isPresent) {
            noteRepository.deleteById(noteID)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{id}")
    fun updateNoteById(@PathVariable("id") noteID: Int, @RequestBody note: Note): ResponseEntity<Note> {
        return noteRepository.findById(noteID).map { noteDetails ->
            val updatedNote: Note = noteDetails.copy(
                title = note.title,
                body = note.body,
                modifyTime = note.modifyTime,
                folderID = note.folderID
            )
            ResponseEntity(noteRepository.save(updatedNote), HttpStatus.OK)
        }.orElse(ResponseEntity<Note>(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}