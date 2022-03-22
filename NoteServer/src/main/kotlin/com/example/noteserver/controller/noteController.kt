package com.example.noteserver.controller

import com.example.noteserver.model.Note
import com.example.noteserver.repository.NoteService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notes")
class NoteResource(val service: NoteService) {
    // From: https://www.baeldung.com/spring-requestmapping#request-param
    @GetMapping
    fun findByFolderID(
        @RequestParam("folderID", defaultValue = "1") folderID: Int
    ): List<Note> =
        if (folderID == 1) {
            service.getAllNotes()
        } else {
            service.findNotesByFolderID(folderID)
        }

    @PostMapping
    fun insert(@RequestBody note: Note) = service.insertNote(note)

    @DeleteMapping
    fun removeByFolderID(
        @RequestParam("folderID", defaultValue = "0") folderID: Int
    ) = when (folderID) {
        0 -> { /* no operation */ }
        1 -> service.removeAllNotes()
        else -> service.removeNotesByFolderID(folderID)
    }


    @DeleteMapping("/{id}")
    fun removeById(@PathVariable("id") noteID: Int) =
        service.removeNoteByID(noteID)

    @PutMapping("/{id}")
    fun updateById(@PathVariable("id") noteID: Int, @RequestBody note: Note) =
        service.updateNoteByID(noteID, note)

    @GetMapping("/{id}")
    fun findByID(@PathVariable("id") noteID: Int): Optional<Note> =
        service.findNoteByID(noteID)
}