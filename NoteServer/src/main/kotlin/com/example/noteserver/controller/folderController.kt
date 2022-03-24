package com.example.noteserver.controller

import com.example.noteserver.model.Folder
import com.example.noteserver.repository.FolderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/folders")
class FolderResource(private val service: FolderService) {
    @GetMapping
    fun index(): List<Folder> = service.getAllFolders()

    @PostMapping
    fun insert(@RequestBody folder: Folder) = service.insertFolder(folder)

    @DeleteMapping("/{id}")
    fun removeById(@PathVariable("id") folderID: Int) = service.removeFolderByID(folderID)

    @PutMapping("/{id}")
    fun updateById(@PathVariable("id") folderID: Int, @RequestBody folder: Folder) =
        service.updateFolderByID(folderID, folder)
}