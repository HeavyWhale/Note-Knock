package com.example.noteserver.repository

import com.example.noteserver.model.Reminder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReminderRepository : JpaRepository<Reminder, Int>