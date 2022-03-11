package com.example.note.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.note.database.entities.Reminder

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg reminders: Reminder)

    @Delete
    fun delete(reminder: Reminder)

    @Update
    fun update(reminder: Reminder)

    // Ascending order
    @Query("SELECT * FROM reminders ORDER BY id ASC")
    fun getAll(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE noteID = :noteID ORDER BY id ASC")
    fun getRemindersByNoteID(noteID: Int): LiveData<List<Reminder>>

    @Query("SELECT id FROM reminders WHERE noteID = :noteID ORDER BY id ASC")
    fun getReminderIDsByNoteID(noteID: Int): List<Int>

    @Query("SELECT * FROM reminders WHERE id = :reminderID")
    fun getReminderByID(reminderID: Int): Reminder

    @Query("SELECT body FROM reminders WHERE id = :id")
    fun getReminderBodyByID(id: Int): String

    @Query("SELECT time FROM reminders WHERE id = :id")
    fun getReminderTimeByID(id: Int): String

    @Query("SELECT reminderOff FROM reminders WHERE id = :id")
    fun getReminderStateByID(id: Int): Boolean

    @Query("SELECT count(*) FROM reminders")
    fun getAllRemindersCount(): Int

    @Query("SELECT count(*) FROM reminders WHERE noteID = :noteID")
    fun getRemindersCountByNoteID(noteID: Int): Int

    @Query("UPDATE reminders SET noteID = :noteID WHERE id = :reminderID")
    fun updateNoteIDByReminderID(noteID: Int, reminderID: Int)
}