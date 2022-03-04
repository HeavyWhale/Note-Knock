package com.example.note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.Model
import com.example.note.entities.Note

class NotesAdapter(private val listener: OnNoteClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_container, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notes = Model.notes
        holder.setNote(notes[position])
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return Model.notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener {

        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textDatetime: TextView = itemView.findViewById(R.id.textDatetime)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val notes = Model.notes
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onNoteClick(notes[position], position)
            }
        }

        fun setNote(note: Note) {
            textTitle.text = note.title
            textDatetime.text = note.modifyDate
        }
    }

    interface OnNoteClickListener {
        fun onNoteClick(note: Note, position: Int)
    }
}