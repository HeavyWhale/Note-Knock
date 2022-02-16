package com.example.note.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import com.example.note.R
import com.example.note.database.Model

//public class MainActivity extends AppCompatActivity {
//    public static final int RE
//}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddNoteMain = findViewById<ImageView>(R.id.imageAddNoteMain)
        imageAddNoteMain.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, CreateNoteActivity::class.java),
                REQUEST_CODE_ADD_NOTE
            )
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE = 1
    }
}