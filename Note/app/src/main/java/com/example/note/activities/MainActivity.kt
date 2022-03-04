package com.example.note.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.R
import com.example.note.adapters.NotesAdapter
import com.example.note.database.Model
import com.example.note.entities.Note

class MainActivity : AppCompatActivity(), NotesAdapter.OnNoteClickListener {

    private val adapter = NotesAdapter(this)
//    private var firstTimeLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddNoteMain = findViewById<ImageView>(R.id.imageAddNoteMain)
        imageAddNoteMain.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivity(intent)
        }

        val imageViewFolders = findViewById<ImageView>(R.id.imageViewFolders)
        imageViewFolders.setOnClickListener {
            val intent = Intent(applicationContext, FolderStructure::class.java)
            startActivity(intent)
        }

        val noteListRecyclerView = findViewById<RecyclerView>(R.id.noteListRecyclerView)

        noteListRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        noteListRecyclerView.adapter = adapter

        Model.notesAdapter = adapter

        if (intent.getBooleanExtra("switchFolder", false)) {
            val folderClickedPosition = intent.getIntExtra("folderClickedPosition", 0)
            switchFolder(folderClickedPosition)
        }
    }

    override fun onNoteClick(note: Note, position: Int) {
        // Change to note's page
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra("clickNote", true)
        intent.putExtra("noteClickedPosition", position)
        startActivity(intent)
    }

    private fun switchFolder(folderClickedPosition: Int) {
        Model.curFolderID = folderClickedPosition
        val currFolderName = Model.name

        // Change title to be current folder's name
        val textView = findViewById<TextView>(R.id.currentFolder)
        textView.text = currFolderName
    }
}