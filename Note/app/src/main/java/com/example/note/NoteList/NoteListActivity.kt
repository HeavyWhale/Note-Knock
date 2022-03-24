@file:Suppress("DEPRECATION")

package com.example.note.NoteList


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.note.EXTRA_FOLDER_ID
import com.example.note.EXTRA_IS_UPDATE
import com.example.note.EXTRA_NOTE_ID
import com.example.note.EditNote.EditNoteActivity
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.Model.deleteNote
import com.example.note.database.entities.Note
import kotlin.properties.Delegates

class NoteListActivity : AppCompatActivity() {

    private var currentFolderID by Delegates.notNull<Int>()

    // Generate notesAdapter for recycler view to apply to each note
    private val noteAdapter = NoteAdapter { note -> updateNoteOnClick(note) }

    override fun onCreate(savedInstanceState: Bundle?) {
        currentFolderID = intent.getIntExtra(EXTRA_FOLDER_ID, Model.DF.ALL_NOTES.id)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        // Find all views
        val insertNoteButton = findViewById<ImageView>(R.id.imageAddNoteMain)
        val viewFoldersButton = findViewById<ImageView>(R.id.imageViewFolders)
        val textFolderName = findViewById<TextView>(R.id.currentFolder)
        val noteListRecyclerView = findViewById<RecyclerView>(R.id.noteListRecyclerView)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        // Set all views
        // let notesAdapter subscribe to any modification on notes
        Model.getNotesByFolderID(currentFolderID).observe(this) { notes ->
            noteAdapter.submitList(notes)
            if (noteAdapter.noteList.isEmpty()) noteAdapter.noteList = notes
        }

        insertNoteButton.setOnClickListener { insertNoteOnClick() }
        viewFoldersButton.setOnClickListener { finish() }
        textFolderName.text = Model.getFolderNameByID(currentFolderID)

        with (noteListRecyclerView) {
            layoutManager =
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
            setNoteTouchListener(this)
            registerForContextMenu(this)
        }

        swipeRefreshLayout.setOnRefreshListener {
            reloadFromServer()
            swipeRefreshLayout.isRefreshing = false
        }

        val inputSearch = findViewById<EditText>(R.id.inputSearch)
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                noteAdapter.cancelTimer()
            }

            override fun afterTextChanged(s: Editable?) {
                noteAdapter.searchNotes(s.toString(), currentFolderID)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        Log.d("NoteList", "onresume --- notify")
        noteAdapter.notifyDataSetChanged()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.order
        return when (item.itemId) {
            1 -> {
                deleteNote(noteAdapter.getNoteAtPosition(position).id)
                true
            }
            else -> super.onContextItemSelected(item)
        }
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


    // From: https://www.raywenderlich.com/1560485-android-recyclerview-tutorial-with-kotlin#:~:text=the%20stars%20shift%3A-,using%20itemtouchhelper,-Sometimes%20you%E2%80%99ll
    // Set item swipe left delete options
    private fun setNoteTouchListener(noteListRecyclerView: RecyclerView) {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteNote(noteAdapter.getNoteAtPosition(position).id)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(noteListRecyclerView)
    }

    private fun reloadFromServer() {
        Model.pullDataFromServer()
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