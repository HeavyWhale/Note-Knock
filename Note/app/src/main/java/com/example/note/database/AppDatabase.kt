package com.example.note.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.note.DATABASE_NAME
import com.example.note.database.dao.FolderDao
import com.example.note.database.dao.NoteDao
import com.example.note.database.dao.ReminderDao
import com.example.note.database.entities.Note
import com.example.note.database.entities.Folder
import com.example.note.database.entities.Reminder

@Database(entities = [Note::class, Folder::class, Reminder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
    abstract fun getFolderDao(): FolderDao
    abstract fun getReminderDao(): ReminderDao

    // Stolen from https://github.com/android/sunflower/blob/91c4e0613e3e1a3c529235dc8ff73589a9047c79/app/src/main/java/com/google/samples/apps/sunflower/data/AppDatabase.kt
    companion object {
        @Volatile var INSTANCE: AppDatabase? = null
            private set

        fun initInstance(context: Context) {
            synchronized(this) {
                context.deleteDatabase(DATABASE_NAME)
                INSTANCE = buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).allowMainThreadQueries().build()
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}