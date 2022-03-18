package com.example.note.database

import android.util.Log
import com.example.note.database.entities.Note
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


object Model {

    /*****************************************************************************
     * Private Properties
     ****************************************************************************/

    private const val baseURL = "http://10.0.2.2:8080"
    private val client = HttpClient(Android)

    /*****************************************************************************
     * Enum subclass for setting up default folders
     ****************************************************************************/

    // Usage:
    //      val folder = DefaultFolders.SHOPPING_LIST
    //      println("The folder \"${folder.printableName}\" has id ${folder.id}" )
    // https://blog.logrocket.com/kotlin-enum-classes-complete-guide/
    enum class DF(val printableName: String) { // DF == "Default Folders"
        ALL_NOTES       ("All Notes"),
        SNIPPETS        ("Snippets"),
        SHOPPING_LIST   ("Shopping List"),
        REMINDERS       ("Reminders");

        val id: Int = ordinal
    }

//    init {
//        addDefaultFolders()
//    }

    /*****************************************************************************
     * Properties
     ****************************************************************************/


    /*****************************************************************************
     * Public Functions
     ****************************************************************************/


    /**************** Notes ****************/    /*****************************************************************************
     * Notes
     ****************************************************************************/
    fun insertNote(title: String, body: String, createTime: Long, folderID: Int) {
        val currentTime = System.currentTimeMillis()
        val jsonNote = Json.encodeToString(Note(0, title, body, createTime, currentTime, folderID))
        val url = URL("$baseURL/notes")

        if (pushDataToHttpServer("POST", jsonNote, url)) {
            Log.d("Model", "Insert note $title to folder $folderID at time $currentTime")
        } else {
            Log.e("Model", "Insert note $title failed")
        }
    }

    // If the id does not exist, do nothing
    fun deleteNote(noteID: Int) {
        val url = URL("$baseURL/notes/$noteID")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "DELETE"

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("Model", "Failed to delete note $noteID")
            }
        }
    }

    fun updateNote(noteID: Int, title: String, body: String, folderID: Int = -1) {
        val note = Note(
            noteID, title, body,
            createTime = 0,
            modifyTime = System.currentTimeMillis(),
            folderID = 0
        )
        val jsonNote = Json.encodeToString(note)
        val url = URL("$baseURL/notes/$noteID")

        if (pushDataToHttpServer("PUT", jsonNote, url)) {
            Log.d("Model", "Update note $noteID")
        } else {
            Log.e("Model", "Update note $noteID failed")
        }
    }

//    fun getAllNotes(): List<Note> {
//        val url = URL("$baseURL/notes")
//        return getListDataFromHttpServer(url)
//    }
//
//    fun getNoteIDByPosition(position: Int, currentFolderID: Int): Int {
//        val notes = if (currentFolderID == 1) getAllNotes() else getNotesByFolderID(currentFolderID)
//        return notes[position].id!!
//    }

    fun getNoteByID(noteID: Int): Note? {
        val url = URL("$baseURL/notes/$noteID")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            doInput = true
            setRequestProperty("Accept", "application/json")

            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = inputStream.bufferedReader().use {
                    it.readText()
                }  // defaults to UTF-8

                Json.decodeFromString(response)
            } else {
                Log.e("Model", "Getting all notes failed")
                null
            }
        }
    }

//    fun getNotesByFolderID(folderID: Int): List<Note> = runBlocking {
//
//    }

    fun getNotesByFolderID(folderID: Int): List<Note> {
        var notes: List<Note>? = null
        val ENDPOINT = if (folderID == 1) "$baseURL/notes" else "$baseURL/notes/$folderID"

        if (folderID == 1) {  // All Notes folder

            val job = CoroutineScope(IO).launch {
                notes = get("$baseURL/notes")
            }
            println("@ getNotesByFolderID $notes")
            return notes ?: emptyList()
        }

        CoroutineScope(IO).launch {
            notes = get("$baseURL/notes/$folderID")
        }

        return notes ?: emptyList()
    }

    /*****************************************************************************
     * Private Functions
     ****************************************************************************/
//
//    private fun addDefaultFolders() {
//        val defaultFolders = DF
//            .values()
//            .map { Folder( 0, it.printableName ) }
//            .toTypedArray()
//        folderDao.insertAll(*defaultFolders)
//    }

    private fun pushDataToHttpServer(requestMSG: String, jsonData: String, url: URL): Boolean {
//        with(url.openConnection() as HttpURLConnection) {
//            requestMethod = request
//            doOutput = true
//            setRequestProperty("Content-Type", "application/json")
//            setRequestProperty("Accept", "application/json")
//
//            // Write to OutputStream
//            val outputStreamWriter = OutputStreamWriter(outputStream)
//            outputStreamWriter.write(jsonData)
//            outputStreamWriter.flush()
//
//            return responseCode == HttpURLConnection.HTTP_OK
//        }

//        client.request(url) {
//            method = requestMSG
//        }
        return true
    }

    private suspend inline fun <reified T> get(url: String): T? {

        try {
            val decodeFromString = Json.decodeFromString<T?>(client.get(url))
            println(decodeFromString)
            return decodeFromString
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

//        with(url.openConnection() as HttpURLConnection) {
//            requestMethod = "GET"
//            doInput = true
//            doOutput = true
//            setRequestProperty("Accept", "application/json")
//            setRequestProperty("Content-Type", "application/json")
//            println("HERE!!!!!!!")
//
//            try {
//                println("HERE getListDataFromHttpServer with responseCode=$responseCode")
//            } catch (e: java.lang.Exception) {
//                println("HERE!!!!!!!!!!!!!")
//                e.printStackTrace()
//            } finally {
//                disconnect()
//            }
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                val response = inputStream.bufferedReader().use {
//                    it.readText()
//                }  // defaults to UTF-8
//
//                return Json.decodeFromString(response)
//            } else {
//                Log.e("Model", "Getting all notes failed")
//                return emptyList()
//            }
//        }
    }

    /*****************************************************************************
     * FUNCTIONS FOR DEBUGGING PURPOSE ONLY
     ****************************************************************************/
//    fun printNotes(firstN: Int = curFolder.notes.size) {
//        curFolder.printNotes(firstN)
//    }
//
//    fun getNoteCounter():   Int = --noteIDCounter
//    fun getFolderCounter(): Int = --folderIDCounter
//
//    fun reset() {
//        folders.clear()
//        folders.addAll( DF
//            .values()
//            .map { Folder( it.id, it.printableName ) }
//            .toMutableList()
//        )
//
//        curFolderID = 0
//        noteIDCounter = 0
//        curFolderID = 0
//    }
}