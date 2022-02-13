package com.example.note.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.ModelWrapper
import com.example.note.database.Note
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity() : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText;
    private lateinit var inputNoteBody: EditText;

    private lateinit var textDateTime: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack = findViewById<ImageView>(R.id.imageBack)
        imageBack.setOnClickListener { onBackPressed(); }

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteBody = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.TextDateTime);

        textDateTime.text = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date());

        val imageSave = findViewById<ImageView>(R.id.imageSave)
        imageSave.setOnClickListener { saveNote(); }
    }

    private fun saveNote() {
        val note = Note(
            null,
            inputNoteTitle.text.toString(),
            inputNoteBody.text.toString(),
            textDateTime.text.toString()
        )
        val modelWrapper = ModelWrapper();
        val model = modelWrapper.Model();
        model.addNote(note);

        onBackPressed();
    }
}