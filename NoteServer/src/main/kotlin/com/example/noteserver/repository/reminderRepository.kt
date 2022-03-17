package com.example.noteserver.repository

import com.example.noteserver.model.Reminder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service

interface ReminderRepository : CrudRepository<Reminder, Int> {
    fun findByOrderByIdAsc(): List<Reminder>
}

@Service
class ReminderService(val db: ReminderRepository) {

    fun getAllReminders(): List<Reminder> = db.findByOrderByIdAsc()

    fun insertReminder(reminder: Reminder) {
        db.save(reminder)
    }

    fun removeReminderByID(reminderID: Int) {
        val reminder = db.findById(reminderID)
        if (reminder.isPresent) {
            db.deleteById(reminderID)
        }
    }

    fun updateReminderById(reminderID: Int, reminder: Reminder) {
        db.findById(reminderID).map { reminderDetails ->
            val updatedReminder: Reminder = reminderDetails.copy(
                body = reminder.body,
                time = reminder.time,
                reminderOff = reminder.reminderOff
            )
            db.save(updatedReminder)
        }
    }
}

