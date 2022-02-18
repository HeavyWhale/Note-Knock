package com.example.note.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.loader.content.AsyncTaskLoader
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.R
import com.example.note.adapters.NotesAdapter
import com.example.note.database.Folder
import com.example.note.database.Model
import com.example.note.database.Note
import java.util.concurrent.Executors

//public class MainActivity extends AppCompatActivity {
//    public static final int RE
//}

class MainActivity : AppCompatActivity() {

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
        noteListRecyclerView.adapter = NotesAdapter

        if (intent.getBooleanExtra("switchFolder", false)) {
            val folderClickedPosition = intent.getIntExtra("folderClickedPosition", 0)
            switchFolder(folderClickedPosition)
        }
    }

    private fun switchFolder(folderClickedPosition: Int) {
        (Model::switchCurrFolder)(folderClickedPosition)
    }

}