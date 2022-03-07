package com.example.note.EditNote

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.*
import com.example.note.database.Model
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText
    private lateinit var textDateTime: TextView

    private var isUpdate by Delegates.notNull<Boolean>()
    private var currentNoteID by Delegates.notNull<Int>()
    private var currentFolderID by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        // Set from received intent
        isUpdate = intent.getBooleanExtra(EXTRA_IS_UPDATE, false)
        currentNoteID = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        currentFolderID = intent.getIntExtra(EXTRA_FOLDER_ID, Model.DF.ALL_NOTES.id)

        // Find all views
        val backButton = findViewById<ImageView>(R.id.imageBack)
        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        val deleteNoteButton = findViewById<ImageView>(R.id.imageDelete)
        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)

        // Set all views
        backButton.setOnClickListener { saveNote() }
        saveNoteButton.setOnClickListener { saveNote() }
        deleteNoteButton.setOnClickListener { deleteNote() }
        textDateTime.text = getCurrentTime()
//        with(backButton) {
//            hideKeyboard()
//            onBackPressed()
//        }

        // Set content of title and body according to received intent
        when (isUpdate) {
            false -> { /* Do nothing */ }
            true -> {
                Model.getNoteByID(currentNoteID).also {
                    inputNoteTitle.setText(it.title)
                    inputNoteBody.setText(it.body)
                }
            }
        }
    }

    private fun saveNote() {
        // save to model only if title or body has text
        if (inputNoteBody.text.isNotEmpty() || inputNoteTitle.text.isNotEmpty()) {
            Model.insertNote(
                noteID = currentNoteID,
                title = inputNoteTitle.text.toString().ifEmpty { "No Title" },
                body = inputNoteBody.text.toString(),
                folderID = currentFolderID
            )
        }
        hideKeyboard()
        finish()
    }

    private fun deleteNote() {
        if (isUpdate) {
            Model.deleteNote(currentNoteID)
        }
        hideKeyboard()
        finish()
    }

    private fun hideKeyboard() {
        if (currentFocus == null) return
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}