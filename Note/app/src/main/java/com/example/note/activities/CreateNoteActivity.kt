package com.example.note.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.Note
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText

    private lateinit var textDateTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack = findViewById<ImageView>(R.id.imageBack)
        imageBack.setOnClickListener { onBackPressed(); }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)

        textDateTime.text = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())

        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        saveNoteButton.setOnClickListener { saveNote(); }
    }

    private fun saveNote() {
        // Set note title to "No Title" if user input for title is empty (no chars & whitespaces)
        val noteTitle = if (inputNoteTitle.text.toString().isEmpty()) inputNoteTitle.text.toString() else "No Title"

        val note = Note(
            null,
            noteTitle,
            inputNoteBody.text.toString(),
            textDateTime.text.toString()
        )

        (Model::addNote)(note)

        onBackPressed()
    }
}