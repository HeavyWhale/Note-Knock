package com.example.note.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.Note
import com.example.note.getCurrentTime

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText

    private lateinit var textDateTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack = findViewById<ImageView>(R.id.imageBack)
        imageBack.setOnClickListener {
            Model.curNotePosition = -1
            onBackPressed()
        }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)

        textDateTime.text = getCurrentTime()

        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        saveNoteButton.setOnClickListener { saveNote(); }

        if (intent.getBooleanExtra("clickNote", false)) {
            val noteClickedPosition = intent.getIntExtra("noteClickedPosition", 0)
            showNote(noteClickedPosition)
        }
    }

    private fun saveNote() {
        // Set note title to "No Title" if user input for title is empty (no chars & whitespaces)
        val noteTitle = inputNoteTitle.text.toString().ifEmpty { "No Title" }

        if (Model.curNotePosition == -1) {
            // Create a new note
            val note = Note(
                null,
                noteTitle,
                inputNoteBody.text.toString(),
                textDateTime.text.toString(),
                getCurrentTime()
            )
            Model.addNote(note)

        } else {
            // Update existing note
            Model.updateNote(noteTitle, inputNoteBody.text.toString())
            Model.curNotePosition = -1
        }

        onBackPressed()
    }

    private fun showNote(noteClickedPosition: Int) {
        val note = Model.notes[noteClickedPosition]

        inputNoteTitle.setText(note.title)
        inputNoteBody.setText(note.body)
        textDateTime.text = note.modifyDate

        Model.curNotePosition = noteClickedPosition
    }
}