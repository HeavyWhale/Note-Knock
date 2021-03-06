package com.example.note.database.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.note.database.entities.BaseEntity
import com.example.note.database.entities.Note
import com.example.note.toPrettyString
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Dao // [D]ata [A]ccess [O]bjects
interface NoteDao : BaseDao {

    /*****************************************************************************
     * Methods auto-generated by Room
     ****************************************************************************/

    // From https://developer.android.com/training/data-storage/room/accessing-data#convenience-update:
    // Room uses the primary key to match passed entity instances to rows
    // in the database. If there is no row with the same primary key,
    // Room makes no changes.

    // From https://jakubpchmiel.com/better-ways-of-using-room/:
    // On the other hand, when you use OnConflictStrategy.REPLACE and the entity
    // exists, it will replace it but also generate a new id (if you use
    // auto-generated ids). Be aware of that, I spent hours on debugging this
    // issue when I assumed that id would stay the same.

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)

    @Delete
    fun deleteAll(vararg notes: Note)

    @Update
    fun update(note: Note)

    @Update
    fun updateAll(vararg notes: Note)

    /*****************************************************************************
     * Methods defined by user
     ****************************************************************************/

    @Query("DELETE FROM notes")
    override fun clear()

    @Query("DELETE FROM notes WHERE folderID = :folderID")
    fun deleteNotesByFolderID(folderID: Int)

    @Query("SELECT * FROM notes WHERE id = :noteID")
    fun getNoteByID(noteID: Int): Note

    @Query("SELECT createTime FROM notes WHERE id = :noteID")
    fun getNoteCreateTimeByID(noteID: Int): Long

    @Query("SELECT folderID FROM notes WHERE id = :noteID")
    fun getNoteFolderIDByID(noteID: Int): Int

    // Descending order from newest to oldest timestamp
    @Query("SELECT * FROM notes ORDER BY modifyTime DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY modifyTime DESC")
    fun getAllNotesList(): List<Note>

    @Query("SELECT * FROM notes WHERE folderID = :folderID ORDER BY modifyTime DESC")
    fun getNotesListByFolderID(folderID: Int): List<Note>

    // Descending order from newest to oldest timestamp
    @Query("SELECT * FROM notes WHERE folderID = :folderID ORDER BY modifyTime DESC")
    fun getNotesByFolderID(folderID: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY createTime DESC")
    fun getNotesOrderedByCreateTime(): List<Note>

    @Query("SELECT * FROM notes ORDER BY modifyTime DESC")
    fun getNotesOrderedByModifyTime(): List<Note>

    @Query("SELECT count(*) FROM notes")
    fun getAllNotesCount(): Int

    @Query("SELECT count(*) FROM notes WHERE folderID = :folderID")
    fun getNotesCountByFolderID(folderID: Int): Int

    @Query("SELECT title FROM notes WHERE id = :noteID")
    fun getNoteTitleByID(noteID: Int): String

    @Query("SELECT body FROM notes WHERE id = :noteID")
    fun getNoteBodyTitleByID(noteID: Int): String

    override fun pullFromServer(baseURL: String) {
        // Clear all local notes
        clear()

        // Fetch notes from server
        var notes: List<Note>? = null
        runBlocking {
            launch {
                notes = Json.decodeFromString(HttpClient().get("$baseURL/notes"))
            }
        }

        // Update notes if non-null
        if (notes != null) {
            Log.d("NoteDao", "Received notes from server: ${notes!!.toPrettyString()}")
            insertAll(*(notes!!.toTypedArray()))
        } else {
            Log.e("NoteDao", "Fetching notes from server failed, received null!!!")
        }
    }
}

fun NoteDao.pushToServer(item: BaseEntity, operation: BaseDao.OPERATION, baseURL: String) {
    if (item !is Note) {
        Log.e("NoteDao", "Received item is of BaseEntity but not a note!!!")
        return
    } else {
        Log.d("NoteDao", "Received note \"$item\" with operation \"${operation.name}\", " +
            "trying pushing to server at endpoint \"$baseURL\"")
    }
    val ENDPOINT = baseURL + when (operation) {
        BaseDao.OPERATION.INSERT -> "/notes"
        BaseDao.OPERATION.MULTIPLE_DELETE -> "/notes?folderID=${item.folderID}"
        else -> "/notes/${item.id}"
    }.also {
        Log.d("NoteDao", "Pushing to endpoint \"$it\"")
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = HttpClient().request(ENDPOINT) {
                method = when (operation) {
                    BaseDao.OPERATION.INSERT -> HttpMethod.Post
                    BaseDao.OPERATION.UPDATE -> HttpMethod.Put
                    BaseDao.OPERATION.DELETE,
                    BaseDao.OPERATION.MULTIPLE_DELETE -> HttpMethod.Delete
                }
                contentType(ContentType.Application.Json)
                body = Json.encodeToString(item)
            }
            if (response.status != HttpStatusCode.OK) {
                Log.e("NoteDao", "Get response's status code ${response.status}!!!")
            }
        } catch (e: Exception) {
            Log.e("NoteDao", "Exception when sending request to server!!!")
            e.printStackTrace()
        }
    }

    Log.d("NoteDao", "Successfully pushed operation \"${operation.name}\" to server")
}