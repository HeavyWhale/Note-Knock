@file:Suppress("DEPRECATION")

package com.example.note.FolderList

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.view.Window
import android.view.inputmethod.InputMethodManager
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

    lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.destroyInstance()
        AppDatabase.initInstance(applicationContext) // initialize database

        setContentView(R.layout.activity_folder_list)

        // Generate folderAdapter for recycler view to apply to each note
        folderAdapter = FolderAdapter { folder -> selectFolderOnClick(folder) }

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

        registerForContextMenu(folderListRecyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        folderAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.order
        return when (item.itemId) {
            1 -> {
                if (position < Model.DF_LENGTH) {
                    Toast.makeText(baseContext, "Default folders cannot be deleted!", Toast.LENGTH_SHORT).show()
                    return true
                }
                Model.deleteFolder(folderAdapter.getFolderAtPosition(position).id)
                folderAdapter.notifyDataSetChanged()
                true
            }
            2 -> {
                if (position < Model.DF_LENGTH) {
                    Toast.makeText(baseContext, "Default folders cannot be renamed!", Toast.LENGTH_SHORT).show()
                    return true
                }
//                val folderID = Model.getFolderIDByPosition(position)
                val folderID = folderAdapter.getFolderAtPosition(position).id
                val folderName = Model.getFolderNameByID(folderID)
                showUpdateFolderDialog(folderID, folderName)
                true
            }
            else -> super.onContextItemSelected(item)
        }
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
                Toast.makeText(baseContext, "Do not leave folder name blank", Toast.LENGTH_SHORT).show()
            } else {
                // Add the new folder
                Model.insertFolder(folderName)
                dialog.dismiss()
                hideKeyboard()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
            hideKeyboard()
        }
        dialog.show()
    }

    private fun showUpdateFolderDialog(folderID: Int, folderName: String) {
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.update_folder_dialog)
        }
        val doneButton = dialog.findViewById<Button>(R.id.update_folder_done_btn)
        val cancelButton = dialog.findViewById<Button>(R.id.update_folder_cancel_btn)
        val editFolderName = dialog.findViewById<EditText>(R.id.curr_folder_name)
        editFolderName.setText(folderName)
        doneButton.setOnClickListener {
            val updatedName = editFolderName.text.toString()
            if (updatedName.isEmpty()) {
                Toast.makeText(baseContext, "Do not leave folder name blank", Toast.LENGTH_SHORT).show()
            } else {
                // Update folder's name
                Model.updateFolder(folderID, updatedName)
                dialog.dismiss()
                hideKeyboard()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
            hideKeyboard()
        }
        dialog.show()
    }

    // Opens EditNoteActivity when RecyclerView item (a note) is clicked, but is updating a previous note
    private fun selectFolderOnClick(folder: Folder) {
        val intent = Intent(applicationContext, NoteListActivity::class.java).apply {
            putExtra(EXTRA_FOLDER_ID, folder.id)
        }
        startActivity(intent)
    }

    private fun hideKeyboard() {
        if (currentFocus == null) return
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}