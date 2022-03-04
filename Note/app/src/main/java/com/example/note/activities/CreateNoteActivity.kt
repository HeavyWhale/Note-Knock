package com.example.note.activities

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.R
import com.example.note.database.Model
import com.example.note.entities.Note
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
            hideKeyboard()
            onBackPressed()
        }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)

        textDateTime.text = getCurrentTime()

        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        saveNoteButton.setOnClickListener { saveNote(); }

        val deleteNoteButton = findViewById<ImageView>(R.id.imageDelete)
        deleteNoteButton.setOnClickListener { deleteNote(); }

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
                0,
                noteTitle,
                inputNoteBody.text.toString(),
                textDateTime.text.toString(),
                getCurrentTime(),
                Model.id
            )
            Model.addNote(note)

        } else {
            // Update existing note
            Model.updateNote(noteTitle, inputNoteBody.text.toString())
            Model.curNotePosition = -1
        }

        hideKeyboard()
        onBackPressed()
    }

    private fun deleteNote() {
        Model.deleteNote()
        Model.curNotePosition = -1

        hideKeyboard()
        onBackPressed()
    }

    private fun showNote(noteClickedPosition: Int) {
        val note = Model.notes[noteClickedPosition]

        inputNoteTitle.setText(note.title)
        inputNoteBody.setText(note.body)
        textDateTime.text = note.modifyDate

        Model.curNotePosition = noteClickedPosition
    }

    private fun hideKeyboard() {
        if (currentFocus == null) return
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}