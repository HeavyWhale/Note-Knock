package com.example.note.EditNote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Display.Mode
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.*
import com.example.note.database.Model
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.Notification.Notification
import com.example.note.database.entities.Reminder
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText
    private lateinit var textDateTime: TextView
    private lateinit var checklistRecyclerView : RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter

    private var isUpdate by Delegates.notNull<Boolean>()
    private var currentNoteID by Delegates.notNull<Int>()
    private var currentFolderID by Delegates.notNull<Int>()
    private var createTime = System.currentTimeMillis()

    private lateinit var previousTitle: String
    private lateinit var previousBody: String

    private val isEdited: Boolean
        get() = inputNoteBody.text.toString() != previousBody ||
            inputNoteTitle.text.toString() != previousTitle ||
            Model.editedReminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        Model.editedReminder = false

        // Set from received intent
        isUpdate = intent.getBooleanExtra(EXTRA_IS_UPDATE, false)
        currentNoteID = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        currentFolderID = intent.getIntExtra(EXTRA_FOLDER_ID, Model.DF.ALL_NOTES.id)

        // Generate checklistAdapter
        checklistAdapter = ChecklistAdapter()

        // Find all views
        val backButton = findViewById<ImageView>(R.id.imageBack)
        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        val deleteNoteButton = findViewById<ImageView>(R.id.imageDelete)
        val addReminderButton = findViewById<ImageView>(R.id.imageAddReminder)
        val redoButton = findViewById<ImageView>(R.id.redo)
        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)
        checklistRecyclerView = findViewById(R.id.checklistRecyclerView)

        // Set all views
        backButton.setOnClickListener { saveNote() }
        saveNoteButton.setOnClickListener { saveNote() }
        deleteNoteButton.setOnClickListener { deleteNote() }
        addReminderButton.setOnClickListener { addReminder() }
        redoButton.setOnClickListener { addNotification() }
        textDateTime.text = createTime.toPrettyTime()
        Model.getRemindersByNoteID(currentNoteID).observe(this) { reminders ->
            checklistAdapter.submitList(reminders)
        }
        checklistRecyclerView.layoutManager =
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        checklistRecyclerView.adapter = checklistAdapter

        // Set content of title, body and timestamp according to received intent
        when (isUpdate) {
            false -> {
                textDateTime.text = createTime.toPrettyTime()
            }
            true -> {
                Model.getNoteByID(currentNoteID).also {
                    inputNoteTitle.setText(it.title); previousTitle = it.title
                    inputNoteBody.setText(it.body); previousBody = it.body
                    textDateTime.text = it.modifyTime.toPrettyTime()
                }
            }
        }
    }


    private fun saveNote() {
        // Save to model only if title or body has text
        if (inputNoteBody.text.isNotEmpty() || inputNoteTitle.text.isNotEmpty()) {
            if (isUpdate) {
                // Update current note
                if (isEdited) {
                    Model.updateNote(
                        noteID = currentNoteID,
                        title = inputNoteTitle.text.toString().ifEmpty { "No Title" },
                        body = inputNoteBody.text.toString()
                    )
                    // May added new reminders
                    updateReminderNoteID(currentNoteID)
                }
            } else {
                // Insert new note

                // Change folderID to Snippets if we are in All Notes folder
                val folderID = if (currentFolderID == 1) 2 else currentFolderID
                val newID = Model.insertNote(
                    title = inputNoteTitle.text.toString().ifEmpty { "No Title" },
                    body = inputNoteBody.text.toString(),
                    createTime = createTime,
                    folderID = folderID
                )
                // Update reminders' note ID
                updateReminderNoteID(newID)
            }

        } else {
            // Since both title and body of the note are empty, we simply delete or
            // discard the current note
            if (isUpdate) {
                Model.deleteNote(currentNoteID)
            }
            Model.deleteRemindersByNoteID(0)
        }
        hideKeyboard()
        finish()
    }

    private fun deleteNote() {
        if (isUpdate) {
            Model.deleteNote(currentNoteID)
        } else {
            // Delete possibly created reminders
            Model.deleteRemindersByNoteID(currentNoteID)
        }
        hideKeyboard()
        finish()
    }

    private fun hideKeyboard() {
        if (currentFocus == null) return
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun addReminder() {
        Model.insertReminder("", "", currentNoteID)
        Model.editedReminder = true
//        checklistAdapter.notifyDataSetChanged()
    }

    private fun addNotification() {
        val intent = Intent(this, Notification::class.java)
        startActivity(intent)
    }

    private fun updateReminderNoteID(noteID: Int) {
        Model.updateRemindersNoteIDByNoteID(currentNoteID, noteID)
    }
}