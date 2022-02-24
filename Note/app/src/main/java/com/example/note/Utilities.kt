package com.example.note

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentTime() =
    SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())