package com.example.noteserver.controller

import com.example.noteserver.model.Reminder
import com.example.noteserver.repository.ReminderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reminders")
class ReminderResource(private val service: ReminderService) {
    @GetMapping
    fun findByNoteID(
        @RequestParam("noteID", defaultValue = "0") noteID: Int
    ): List<Reminder> =
        if (noteID == 0) {
            service.getAllReminders()
        } else {
            service.findRemindersByNoteID(noteID)
        }

    @PostMapping
    fun insert(@RequestBody reminder: Reminder) =
        service.insertReminder(reminder)

    @DeleteMapping
    fun removeAllReminders() {
        service.removeAllReminders()
    }

    @DeleteMapping(
        params = ["noteID"]
    )
    fun removeByNoteID(
        @RequestParam("noteID", defaultValue = "0") noteID: Int
    ) = when (noteID) {
        0 -> { /* no operation */ }
        else -> service.removeReminderByNoteID(noteID)
    }

    @PutMapping( params = ["oldNoteID", "newNoteID"] )
    fun updateNoteIDbyNoteID(
        @RequestParam("oldNoteID", defaultValue = "0") oldNoteID: Int,
        @RequestParam("newNoteID", defaultValue = "0") newNoteID: Int
    ) {
        service.updateRemindersNoteIDByNoteID(oldNoteID, newNoteID)
    }

    @DeleteMapping("/{id}")
    fun removeById(@PathVariable("id") reminderID: Int) =
        service.removeReminderByID(reminderID)

    @PutMapping("/{id}")
    fun updateById(@PathVariable("id") reminderID: Int, @RequestBody reminder: Reminder) =
        service.updateReminderByID(reminderID, reminder)
}