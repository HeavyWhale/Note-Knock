package com.example.note.EditNote

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.note.*
import com.example.note.database.Model
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.Notification.Notification
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteBody: EditText
    private lateinit var textDateTime: TextView
    private lateinit var noteImage: ImageView
    private lateinit var checklistRecyclerView : RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter

    private var isUpdate by Delegates.notNull<Boolean>()
    private var currentNoteID by Delegates.notNull<Int>()
    private var currentFolderID by Delegates.notNull<Int>()
    private var createTime = System.currentTimeMillis()

    private lateinit var selectedImagePath: String

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

        selectedImagePath = ""

        // Find all views
        val backButton = findViewById<ImageView>(R.id.imageBack)
        val saveNoteButton = findViewById<ImageView>(R.id.imageSave)
        val deleteNoteButton = findViewById<ImageView>(R.id.imageDelete)
        val addReminderButton = findViewById<ImageView>(R.id.imageAddReminder)
        val addImageButton = findViewById<ImageView>(R.id.addImage)
        val redoButton = findViewById<ImageView>(R.id.redo)
        val wordCount = findViewById<TextView>(R.id.wordCount)
        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteBody = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.TextDateTime)
        noteImage = findViewById(R.id.noteImage)
        checklistRecyclerView = findViewById(R.id.checklistRecyclerView)


        inputNoteBody.addTextChangedListener(object : TextWatcher {

            //override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val text = inputNoteBody.text.toString().trim()
                //text = text.replace("\n", " ")
                var textSize = 0
                if (text.isNotEmpty()){
                    textSize = text.split("\\s+".toRegex()).size
                }
                //val textArray = text.split("\\s")
                wordCount.text = "Words: " + textSize
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        // Set all views
        backButton.setOnClickListener { saveNote() }
        saveNoteButton.setOnClickListener { saveNote() }
        deleteNoteButton.setOnClickListener { deleteNote() }
        addReminderButton.setOnClickListener { addReminder() }
        addImageButton.setOnClickListener { addImage() }
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

    private fun addImage() {
        if (askForPermissions()) {
            Log.d("Note", "Add image Button clicked.")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
    }

    /*
    1) Pick Image From Gallery in Kotlin – Android
    https://handyopinion.com/pick-image-from-gallery-in-kotlin-android/
    2) OnActivityResult deprecated
    https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
    */
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val selectedImageURI = data?.data
            if(selectedImageURI != null) {
                try {
                    // InputStream
                    var inputStream = contentResolver.openInputStream(selectedImageURI)
                    // Bitmap
                    var bitmap = BitmapFactory.decodeStream(inputStream)
                    noteImage.setImageBitmap(bitmap)
                    selectedImagePath = getPathFromUri(selectedImageURI)
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            // noteImage.setImageURI(data?.data)
            Log.d("Note", "Get image ${data?.data}.")
        }
    }

    private fun getPathFromUri(contentUri: Uri): String {
        var filePath: String
        var cursor = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path.toString()
        } else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    /*
     Ask runtime permission to access system image,
     according to https://handyopinion.com/ask-runtime-permission-in-kotlin-android/
     REQUEST_CODE = 1
    */
    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, READ_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(READ_EXTERNAL_STORAGE), 1)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // READ_EXTERNAL_STORAGE
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { _, _ ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }
}