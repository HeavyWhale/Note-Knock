package com.example.note.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.note.dao.NoteDao
import com.example.note.entities.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDb : RoomDatabase() {
    abstract fun notesDao() : NoteDao

    companion object {
        private var INSTANCE: NotesDb? = null

        fun getInstance(context: Model): NotesDb? {
            if (INSTANCE == null) {
                synchronized(NotesDb::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        NotesDb::class.java, "user.db").allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}