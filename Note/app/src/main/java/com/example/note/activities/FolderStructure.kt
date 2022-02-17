package com.example.note.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.R
import com.example.note.adapters.FolderAdapter
import com.example.note.database.Model

class FolderStructure : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_structure)

        val imageAddFolder = findViewById<ImageView>(R.id.imageAddFolder)
        imageAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        val folderListRecyclerView = findViewById<RecyclerView>(R.id.folderListRecyclerView)

        folderListRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        folderListRecyclerView.adapter = FolderAdapter
    }

    private fun showCreateFolderDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.create_folder_dialog)
        val createBtn = dialog.findViewById<Button>(R.id.createBtn)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)
        createBtn.setOnClickListener {
            val folderName = dialog.findViewById<EditText>(R.id.folder_name).text.toString()
            if (folderName.isEmpty()) {
                Toast.makeText(baseContext, "Please name your folder!", Toast.LENGTH_SHORT).show()
            } else {
                // Add the new folder
                (Model::addFolder)(folderName)
                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}