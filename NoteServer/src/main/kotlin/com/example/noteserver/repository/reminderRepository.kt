package com.example.noteserver.repository

import com.example.noteserver.model.Reminder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ReminderRepository : CrudRepository<Reminder, Int> {
    fun findByOrderByIdAsc(): List<Reminder>

    fun findRemindersByNoteID(noteID: Int): List<Reminder>

    @Transactional
    fun deleteRemindersByNoteID(noteID: Int)

}

@Service
class ReminderService(val db: ReminderRepository) {

    fun getAllReminders(): List<Reminder> = db.findByOrderByIdAsc()

    fun findRemindersByNoteID(noteID: Int): List<Reminder> {
        return db.findRemindersByNoteID(noteID)
    }

    fun insertReminder(reminder: Reminder) = db.save(reminder)

    fun removeAllReminders() {
        db.deleteAll()
    }

    fun removeReminderByID(reminderID: Int) {
        val reminder = db.findById(reminderID)
        if (reminder.isPresent) {
            db.deleteById(reminderID)
        }
    }

    fun removeReminderByNoteID(noteID: Int) {
        db.deleteRemindersByNoteID(noteID)
    }

    fun updateReminderByID(reminderID: Int, reminder: Reminder) {
        removeReminderByID(reminderID)
        db.save(reminder)
    }

    fun updateRemindersNoteIDByNoteID(oldNoteID: Int, newNoteID: Int) {
        db.findRemindersByNoteID(oldNoteID).map {
            db.save(it.copy(noteID = newNoteID))
        }
    }
}

