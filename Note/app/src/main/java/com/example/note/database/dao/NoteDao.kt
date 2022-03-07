package com.example.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.note.database.entities.Note

@Dao // [D]ata [A]ccess [O]bjects
interface NoteDao {

    // https://developer.android.com/training/data-storage/room/accessing-data#convenience-update
    // Room uses the primary key to match passed entity instances to rows
    // in the database. If there is no row with the same primary key,
    // Room makes no changes.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg notes: Note)

    // Only need to construct a note with ID = targetID, other fields are ignored
    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM notes WHERE id = :noteID")
    fun getNoteByID(noteID: Int): Note

    @Query("SELECT createTime FROM notes WHERE id = :noteID")
    fun getNoteCreateTimeByID(noteID: Int): String

    @Query("SELECT folderID FROM notes WHERE id = :noteID")
    fun getNoteFolderIDByID(noteID: Int): Int

    // Descending order
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    // Descending order
    @Query("SELECT * FROM notes WHERE folderID = :folderID ORDER BY id DESC")
    fun getNotesByFolderID(folderID: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY createTime DESC")
    fun getNotesOrderedByCreateTime(): List<Note>

    @Query("SELECT * FROM notes ORDER BY modifyTime DESC")
    fun getNotesOrderedByModifyTime(): List<Note>

    @Query("SELECT count(*) FROM notes")
    fun getAllNotesCount(): Int

    @Query("SELECT count(*) FROM notes WHERE folderID = :folderID")
    fun getNotesCountByFolderID(folderID: Int): Int
}