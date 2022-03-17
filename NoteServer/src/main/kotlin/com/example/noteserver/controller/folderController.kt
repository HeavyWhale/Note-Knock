package com.example.noteserver.controller

import com.example.noteserver.repository.FolderRepository
import com.example.noteserver.model.Folder
import com.example.noteserver.model.Note
import com.example.noteserver.repository.FolderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/folders")
class FolderResource(private val service: FolderService) {
    @GetMapping
    fun index(): List<Folder> {
        return service.getAllFolders()
    }

    @PostMapping
    fun post(@RequestBody folder: Folder) {
        service.insertFolder(folder)
    }

    @DeleteMapping("/{id}")
    fun removeNoteById(@PathVariable("id") folderID: Int) {
        service.removeFolderByID(folderID)
    }

    @PutMapping("/{id}")
    fun updateFolderById(@PathVariable("id") folderID: Int, @RequestBody folder: Folder) {
        service.updateFolderById(folderID, folder)
    }
}