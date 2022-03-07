@file:Suppress("DEPRECATION")

package com.example.note.FolderList

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.EXTRA_FOLDER_ID
import com.example.note.NoteList.NoteListActivity
import com.example.note.R
import com.example.note.database.AppDatabase
import com.example.note.database.Model
import com.example.note.database.entities.Folder

class FolderListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDatabase.destroyInstance()
        AppDatabase.initInstance(applicationContext) // initialize database
        setContentView(R.layout.activity_folder_structure)

        // Generate folderAdapter for recycler view to apply to each note
        val folderAdapter = FolderAdapter { folder -> selectFolderOnClick(folder) }

        // Find all views
        val addFolderButton = findViewById<ImageView>(R.id.imageAddFolder)
        val folderListRecyclerView = findViewById<RecyclerView>(R.id.folderListRecyclerView)

        // Set all views
        // let notesAdapter subscribe to any modification on folders
        Model.getAllFolders().observe(this) { folders ->
            folderAdapter.submitList(folders)
        }
        addFolderButton.setOnClickListener {
            showCreateFolderDialog()
        }
        folderListRecyclerView.adapter = folderAdapter
        folderListRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun showCreateFolderDialog() {
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.create_folder_dialog)
        }
        val createButton = dialog.findViewById<Button>(R.id.createBtn)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelBtn)
        createButton.setOnClickListener {
            val folderName = dialog.findViewById<EditText>(R.id.folder_name).text.toString()
            if (folderName.isEmpty()) {
                Toast.makeText(baseContext, "Please name your folder!", Toast.LENGTH_SHORT).show()
            } else {
                // Add the new folder
                Model.addFolder(folderName)
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // Opens EditNoteActivity when RecyclerView item (a note) is clicked, but is updating a previous note
    private fun selectFolderOnClick(folder: Folder) {
        val intent = Intent(applicationContext, NoteListActivity::class.java).apply {
            putExtra(EXTRA_FOLDER_ID, folder.id)
        }
        startActivity(intent)
    }
}