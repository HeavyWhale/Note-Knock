package com.example.noteserver.controller

import com.example.noteserver.model.Note
import com.example.noteserver.model.Reminder
import com.example.noteserver.repository.ReminderRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reminders")
class ReminderResource(private val reminderRepository: ReminderRepository) {
    @GetMapping
    fun index(): ResponseEntity<List<Reminder>> {
        val reminders = reminderRepository.findAll()
        if (reminders.isEmpty()) {
            return ResponseEntity<List<Reminder>>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<List<Reminder>>(reminders, HttpStatus.OK)
    }

    @PostMapping
    fun post(@RequestBody reminder: Reminder) {
        reminderRepository.save(reminder)
    }

    @DeleteMapping("/{id}")
    fun removeNoteById(@PathVariable("id") reminderID: Int): ResponseEntity<Void> {
        val note = reminderRepository.findById(reminderID)
        if (note.isPresent) {
            reminderRepository.deleteById(reminderID)
            return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PutMapping("/{id}")
    fun updateNoteById(@PathVariable("id") reminderID: Int, @RequestBody reminder: Reminder): ResponseEntity<Reminder> {
        return reminderRepository.findById(reminderID).map { reminderDetails ->
            val updatedReminder: Reminder = reminderDetails.copy(
                body = reminder.body,
                time = reminder.time,
                reminderOff = reminder.reminderOff
            )
            ResponseEntity(reminderRepository.save(updatedReminder), HttpStatus.OK)
        }.orElse(ResponseEntity<Reminder>(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}