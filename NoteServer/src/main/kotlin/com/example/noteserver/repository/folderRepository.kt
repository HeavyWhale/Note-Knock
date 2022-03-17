package com.example.noteserver.repository

import com.example.noteserver.model.Folder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service

interface FolderRepository : CrudRepository<Folder, Int> {

    fun findByOrderByIdAsc(): List<Folder>
}

@Service
class FolderService(val db: FolderRepository) {

    fun getAllFolders(): List<Folder> = db.findByOrderByIdAsc()

    fun insertFolder(folder: Folder) {
        db.save(folder)
    }

    fun removeFolderByID(folderID: Int) {
        val folder = db.findById(folderID)
        if (folder.isPresent) {
            db.deleteById(folderID)
        }
    }

    fun updateFolderById(folderID: Int, folder: Folder) {
        db.findById(folderID).map { folderDetails ->
            val updatedFolder: Folder = folderDetails.copy(
                name = folder.name
            )
            db.save(updatedFolder)
        }
    }
}
