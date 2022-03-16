package com.example.noteserver.repository

import com.example.noteserver.model.Folder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FolderRepository : JpaRepository<Folder, Int>