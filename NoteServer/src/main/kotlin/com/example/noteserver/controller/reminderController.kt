package com.example.noteserver.controller

import com.example.noteserver.model.Note
import com.example.noteserver.model.Reminder
import com.example.noteserver.repository.ReminderRepository
import com.example.noteserver.repository.ReminderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reminders")
class ReminderResource(private val service: ReminderService) {
    @GetMapping
    fun index(): List<Reminder> {
        return service.getAllReminders()
    }

    @PostMapping
    fun post(@RequestBody reminder: Reminder) {
        service.insertReminder(reminder)
    }

    @DeleteMapping("/{id}")
    fun removeReminderById(@PathVariable("id") reminderID: Int) {
        service.removeReminderByID(reminderID)
    }

    @PutMapping("/{id}")
    fun updateReminderById(@PathVariable("id") reminderID: Int, @RequestBody reminder: Reminder) {
        service.updateReminderById(reminderID, reminder)
    }
}