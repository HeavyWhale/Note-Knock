package com.example.noteserver.controller

import com.example.noteserver.model.Note
import com.example.noteserver.repository.NoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notes")
class NoteResource(val service: NoteService) {
    @GetMapping
    fun index(): List<Note> = service.getAllNotes()

    @PostMapping
    fun post(@RequestBody note: Note) {
        service.insertNote(note)
    }

    @DeleteMapping("/{id}")
    fun removeNoteById(@PathVariable("id") noteID: Int) {
        service.removeNoteByID(noteID)
    }

    @PutMapping("/{id}")
    fun updateNoteById(@PathVariable("id") noteID: Int, @RequestBody note: Note) {
        service.updateNoteById(noteID, note)
    }

    @GetMapping("/{id}")
    fun findNoteById(@PathVariable("id") noteID: Int): Optional<Note> {
        return service.findNoteById(noteID)
    }

    @GetMapping("/folder/{folderID}")
    fun findNotesByFolderId(@PathVariable("folderID") folderID: Int): List<Note> {
        return service.findNotesByFolderId(folderID)
    }
}