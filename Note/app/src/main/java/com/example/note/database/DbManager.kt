package com.example.note.database

import android.provider.BaseColumns
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DbManager {
    val dbConn = connect()

    private fun connect(): Connection? {
        var conn: Connection? = null
        try {
            val url = "jdbc:sqlite:allnotes.db"
            conn = DriverManager.getConnection(url)
            Log.d("Database", "INFO: Connection to SQLite has been established.")
        } catch (e: SQLException) {
            Log.d("Database", "ERROR: ${e.message}")
        }

        // Add tables to db
        val createNotesTableQuery =
            "CREATE TABLE Notes (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "Title TEXT," +
                    "Body TEXT," +
                    "CreateDate TEXT," +
                    "ModifyDate TEXT," +
                    "ParentFolder INTEGER PRIMARY KEY)"
        val createFoldersTableQuery =
            "CREATE TABLE Folders (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "Name TEXT)"

        if (conn != null) {
            with (conn) {
                createStatement().execute(createNotesTableQuery)
                createStatement().execute(createFoldersTableQuery)
            }
        }

        return conn
    }
}