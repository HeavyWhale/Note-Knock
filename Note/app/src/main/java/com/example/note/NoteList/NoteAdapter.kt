package com.example.note.NoteList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.entities.Note

class NoteAdapter(private val onClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NotesDiffCallback) {

    class NoteViewHolder(itemView: View, val onClick: (Note) -> Unit) :
        RecyclerView.ViewHolder(itemView)  {

        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDatetime: TextView = itemView.findViewById(R.id.textDatetime)
        private var currentNote: Note? = null

        init {
            itemView.setOnClickListener { currentNote?.let { onClick(it) } }
        }

        fun bind(note: Note) {
            with (note) {
                currentNote = this
                textTitle.text = title
                textDatetime.text = modifyTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container, parent, false)
        return NoteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
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