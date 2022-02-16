package com.example.note.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.example.note.R

class FolderStructure : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_structure)

        val imageAddFolder = findViewById<ImageView>(R.id.imageAddFolder)
        imageAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }
    }

    private fun showCreateFolderDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.create_folder_dialog)
        val createBtn = dialog.findViewById<Button>(R.id.createBtn)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)
        createBtn.setOnClickListener { dialog.dismiss() }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}