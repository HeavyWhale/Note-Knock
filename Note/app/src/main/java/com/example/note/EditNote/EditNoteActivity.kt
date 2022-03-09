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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText
    private lateinit var textDateTime: TextView

    private var isUpdate by Delegates.notNull<Boolean>()
    private var currentNoteID by Delegates.notNull<Int>()
    private var currentFolderID by Delegates.notNull<Int>()
    private var createTime = System.currentTimeMillis()

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

//        with(backButton) {
//            hideKeyboard()
//            onBackPressed()
//        }

        val format = SimpleDateFormat("EEEE, yyyy-MM-dd at HH:mm a", Locale.US)
        // Set content of title, body and timestamp according to received intent
        when (isUpdate) {
            false -> {
                textDateTime.text = format.format(Date(createTime))
            }
            true -> {
                Model.getNoteByID(currentNoteID).also {
                    inputNoteTitle.setText(it.title)
                    inputNoteBody.setText(it.body)
                    textDateTime.text = format.format(Date(it.modifyTime))
                }
            }
        }
    }

    private fun saveNote() {
        // Save to model only if title or body has text
        if (inputNoteBody.text.isNotEmpty() || inputNoteTitle.text.isNotEmpty()) {
            if (isUpdate) {
                // Update current note
                Model.updateNote(
                    noteID = currentNoteID,
                    title = inputNoteTitle.text.toString().ifEmpty { "No Title" },
                    body = inputNoteBody.text.toString()
                )
            } else {
                // Insert new note

                // Change folderID to Snippets if we are in All Notes folder
                val folderID = if (currentFolderID == 1) 2 else currentFolderID
                Model.insertNote(
                    noteID = currentNoteID,
                    title = inputNoteTitle.text.toString().ifEmpty { "No Title" },
                    body = inputNoteBody.text.toString(),
                    createTime = createTime,
                    folderID = folderID
                )
            }

        } else {
            // Since both title and body of the note are empty, we simply delete or
            // discard the current note
            if (isUpdate) { Model.deleteNote(currentNoteID) }
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