package com.example.note

import java.text.SimpleDateFormat
import java.util.*

const val DATABASE_NAME = "notes-db"
const val EXTRA_IS_UPDATE = "isUpdate"
const val EXTRA_NOTE_ID = "noteID"
const val EXTRA_FOLDER_ID = "folderID"

fun getCurrentTime() =
    SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())