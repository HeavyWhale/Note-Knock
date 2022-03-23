package com.example.note.database.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.note.database.entities.BaseEntity
import com.example.note.database.entities.Folder
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
interface FolderDao : BaseDao {

    /*****************************************************************************
     * Methods auto-generated by Room
     ****************************************************************************/

    // https://developer.android.com/training/data-storage/room/accessing-data#convenience-update
    // Room uses the primary key to match passed entity instances to rows
    // in the database. If there is no row with the same primary key,
    // Room makes no changes.

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(folder: Folder): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg folders: Folder)

    @Delete
    fun delete(folder: Folder)

    @Delete
    fun deleteAll(vararg folders: Folder)

    @Update
    fun update(folder: Folder)

    @Update
    fun updateAll(vararg folders: Folder)

    /*****************************************************************************
     * Methods defined by user
     ****************************************************************************/

    @Query("DELETE FROM folders")
    override fun clear()

    @Query("SELECT * FROM folders ORDER BY id ASC")
    fun getFD(): List<Folder>

    // Descending order
    @Query("SELECT * FROM folders ORDER BY id ASC")
    fun getAllFolders(): LiveData<List<Folder>>

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getFolderByID(id: Int): Folder

    @Query("SELECT name FROM folders WHERE id = :id")
    fun getFolderNameByID(id: Int): String

    @Query("SELECT id FROM folders WHERE name = :name")
    fun getFolderIDByName(name: String): Int

    @Query("SELECT count(*) FROM folders")
    fun getFolderCounts(): Int

    override fun pullFromServer(baseURL: String) {
        // Clear all local folders
        clear()

        // Fetch folders from server
        var folders: List<Folder>? = null
        runBlocking {
            launch {
                folders = Json.decodeFromString(HttpClient().get("$baseURL/folders"))
            }
        }

        // Update folders if non-null
        if (folders != null) {
            Log.d("FolderDao", "Received folders from server: ${folders!!.toPrettyString()}")
            insertAll(*(folders!!.toTypedArray()))
        } else {
            Log.e("FolderDao", "Fetching folders from server failed, received null!!!")
        }
    }
}

fun FolderDao.pushToServer(item: BaseEntity, operation: BaseDao.OPERATION, baseURL: String) {
    if (item !is Folder) {
        Log.e("FolderDao", "Received item is of BaseEntity but not a folder!!!")
        return
    } else {
        Log.d("FolderDao", "Received folder \"$item\" with operation \"${operation.name}\", " +
            "trying pushing to server at endpoint \"$baseURL\"")
    }
    val ENDPOINT = baseURL + when (operation) {
        BaseDao.OPERATION.INSERT -> "/folders"
        else -> "/folders/${item.id}"
    }.also {
        Log.d("FolderDao", "Pushing to endpoint \"$it\"")
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
                Log.e("FolderDao", "Get response's status code ${response.status}!!!")
            }
        } catch (e: Exception) {
            Log.e("FolderDao", "Exception when sending request to server!!!")
            e.printStackTrace()
        }
    }

    Log.d("FolderDao", "Successfully pushed operation \"${operation.name}\" to server")
}