package com.example.noteserver.controller

import com.example.noteserver.repository.FolderRepository
import com.example.noteserver.model.Folder
import com.example.noteserver.model.Note
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/folders")
class FolderResource(private val folderRepository: FolderRepository) {
    @GetMapping
    fun index(): ResponseEntity<List<Folder>> {
        val folders = folderRepository.findAll()
        if (folders.isEmpty()) {
            return ResponseEntity<List<Folder>>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<List<Folder>>(folders, HttpStatus.OK)
    }

    @PostMapping
    fun post(@RequestBody folder: Folder) {
        folderRepository.save(folder)
    }

    @DeleteMapping("/{id}")
    fun removeNoteById(@PathVariable("id") folderID: Int): ResponseEntity<Void> {
        val note = folderRepository.findById(folderID)
        if (note.isPresent) {
            folderRepository.deleteById(folderID)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{id}")
    fun updateNoteById(@PathVariable("id") folderID: Int, @RequestBody folder: Folder): ResponseEntity<Folder> {
        return folderRepository.findById(folderID).map { folderDetails ->
            val updatedFolder: Folder = folderDetails.copy(
                name = folder.name
            )
            ResponseEntity(folderRepository.save(updatedFolder), HttpStatus.OK)
        }.orElse(ResponseEntity<Folder>(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}