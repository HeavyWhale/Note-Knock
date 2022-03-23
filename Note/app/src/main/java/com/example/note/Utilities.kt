package com.example.note

import java.text.SimpleDateFormat
import java.util.*

const val DATABASE_NAME = "notes-db"
const val EXTRA_IS_UPDATE = "isUpdate"
const val EXTRA_NOTE_ID = "noteID"
const val EXTRA_FOLDER_ID = "folderID"
const val EXTRA_REMINDER_ID = "reminderID"

fun Any.toPrettyString(): String =
    toString().replace("), ", "),\n ")

fun Long.toPrettyTime(): String =
    SimpleDateFormat("EEEE, yyyy-MM-dd HH:mm a", Locale.getDefault()).format(Date(this))