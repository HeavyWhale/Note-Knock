package com.example.note.NoteList

import android.util.Log
import android.view.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.entities.Note
import com.example.note.toPrettyString
import com.example.note.toPrettyTime
import java.util.*
import kotlin.collections.ArrayList


class NoteAdapter(private val onClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NotesDiffCallback) {

    var noteList: List<Note> = emptyList()
    private var timer: Timer? = null

    class NoteViewHolder(itemView: View, val onClick: (Note) -> Unit) :
        RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener  {

        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDatetime: TextView = itemView.findViewById(R.id.textDatetime)
        private var currentNote: Note? = null

        init {
            itemView.setOnClickListener { currentNote?.let { onClick(it) } }
            itemView.setOnCreateContextMenuListener(this)
        }

        fun bind(note: Note) {
            with (note) {
                currentNote = this
                textTitle.text = title
                textDatetime.text = modifyTime.toPrettyTime()
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu.add(0, 1, bindingAdapterPosition, R.string.delete)   // 0=?, 1=id
        }
    }

    fun getNoteAtPosition(position: Int): Note {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container, parent, false)
        return NoteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun searchNotes(keyword: String, currentFolderID: Int) {
        timer = Timer()
        timer!!.schedule(object: TimerTask() {
            override fun run() {
                val notes = Model.getNotesListByFolderID(currentFolderID)
                val result = if (keyword.trim().isEmpty()) {
//                    Log.d("NoteAdapter", "all notes: ${notes.toPrettyString()}")
                    notes
                } else {
                    val filteredNotes = ArrayList<Note>()
                    Log.d("NoteAdapter", "search in $notes folder $currentFolderID with keyword $keyword")
                    val lowercaseKeyword = keyword.lowercase()
                    for (note in notes) {
                        if (note.title.lowercase().contains(lowercaseKeyword)
                            || note.body.lowercase().contains(lowercaseKeyword)) {
                            filteredNotes.add(note)
                        }
                    }
//                    Log.d("NoteAdapter", "filteredNotes: ${filteredNotes.toPrettyString()}")
                    filteredNotes
                }
                this@NoteAdapter.submitList(result)
            }
        }, 500)
    }

    fun cancelTimer() {
        timer?.cancel()
    }
}

object NotesDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.title == newItem.title &&
            oldItem.body == newItem.body &&
            oldItem.folderID == newItem.folderID
    }
}