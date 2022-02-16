package com.example.note.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.loader.content.AsyncTaskLoader
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.R
import com.example.note.adapters.NotesAdapter
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

        val noteListRecyclerView = findViewById<RecyclerView>(R.id.noteListRecyclerView)

        noteListRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        noteListRecyclerView.adapter = NotesAdapter

//        listNotes()
    }

//    private fun listNotes() {
//
//        val myExecutor = Executors.newSingleThreadExecutor()
//        val myHandler = Handler(Looper.getMainLooper())
//
//        myExecutor.execute {
//            // Do something in background (back-end process)
//        }
//
//        myHandler.post {
//            // Do something in UI (front-end process)
//            // TODO: 14:00min tutorial 4
//            if ((Model::getSize)() == 0) {
//                noteList.addAll(notes)
//                notesAdapter.notifyDataSetChanged()
//            } else {
//                noteList.add(0, notes[0])
//                notesAdapter.notifyItemInserted(0)
//            }
//            noteListRecyclerView.smoothScrollToPosition(0)
//        }
//
//    }
}