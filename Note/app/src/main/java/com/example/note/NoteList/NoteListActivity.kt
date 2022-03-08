@file:Suppress("DEPRECATION")

package com.example.note.NoteList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.EXTRA_FOLDER_ID
import com.example.note.FolderList.FolderListActivity
import com.example.note.EXTRA_IS_UPDATE
import com.example.note.EXTRA_NOTE_ID
import com.example.note.R
import com.example.note.EditNote.EditNoteActivity
import com.example.note.database.Model
import com.example.note.database.entities.Note
import org.w3c.dom.Text
import kotlin.properties.Delegates

class NoteListActivity : AppCompatActivity() {

    private var currentFolderID by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        currentFolderID = intent.getIntExtra(EXTRA_FOLDER_ID, 0)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate notesAdapter for recycler view to apply to each note
        val noteAdapter = NoteAdapter { note -> updateNoteOnClick(note) }

        // Find all views
        val insertNoteButton = findViewById<ImageView>(R.id.imageAddNoteMain)
        val viewFoldersButton = findViewById<ImageView>(R.id.imageViewFolders)
        val textFolderName = findViewById<TextView>(R.id.currentFolder)
        val noteListRecyclerView = findViewById<RecyclerView>(R.id.noteListRecyclerView)

        // Set all views
        // let notesAdapter subscribe to any modification on notes
        Model.getNotesByFolderID(currentFolderID).observe(this) { notes ->
            noteAdapter.submitList(notes)
        }
        insertNoteButton.setOnClickListener {
            insertNoteOnClick()
        }
        viewFoldersButton.setOnClickListener {
            finish()
        }
        textFolderName.text = Model.getFolderNameByID(currentFolderID)
        noteListRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        noteListRecyclerView.adapter = noteAdapter

//        noteListRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

//        if (intent.getBooleanExtra("switchFolder", false)) {
//            val folderClickedPosition = intent.getIntExtra("folderClickedPosition", 0)
//            switchFolder(folderClickedPosition)
//        }
    }

    // Opens EditNoteActivity when the add note button is clicked, but is inserting a new note
    private fun insertNoteOnClick() {
        val intent = Intent(applicationContext, EditNoteActivity::class.java).apply {
            putExtra(EXTRA_IS_UPDATE, false)
            putExtra(EXTRA_FOLDER_ID, currentFolderID)
        }
        startActivity(intent)
    }

    // Opens EditNoteActivity when RecyclerView item (a note) is clicked, but is updating a previous note
    private fun updateNoteOnClick(note: Note) {
        val intent = Intent(applicationContext, EditNoteActivity::class.java).apply {
            putExtra(EXTRA_IS_UPDATE, true)
            putExtra(EXTRA_NOTE_ID, note.id)
            putExtra(EXTRA_FOLDER_ID, currentFolderID)
        }
        startActivity(intent)
    }

    // Returned from EditNoteActivity
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK) {
//            data?.let { // just make sure data is non-null
//
//            }
//        }
//    }

//    private fun switchFolder(folderClickedPosition: Int) {
//        Model.curFolderID = folderClickedPosition
//        val currFolderName = Model.name
//
//        // Change title to be current folder's name
//        val textView = findViewById<TextView>(R.id.currentFolder)
//        textView.text = currFolderName
//    }

//    companion object {
//        const val REQUEST_CODE_ADD_NOTE = 1
//        const val REQUEST_CODE_UPDATE_NOTE = 2
//    }
}