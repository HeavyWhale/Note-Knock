package com.example.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.note.database.entities.Folder

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(folder: Folder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg folders: Folder)

    @Delete
    fun delete(folder: Folder)

    @Update
    fun update(folder: Folder)

    // Descending order
    @Query("SELECT * FROM folders ORDER BY id ASC")
    fun getAll(): LiveData<List<Folder>>

    @Query("SELECT name FROM folders WHERE id = :id")
    fun getFolderNameByID(id: Int): String

    @Query("SELECT id FROM folders WHERE name = :name")
    fun getFolderIDByName(name: String): Int
}