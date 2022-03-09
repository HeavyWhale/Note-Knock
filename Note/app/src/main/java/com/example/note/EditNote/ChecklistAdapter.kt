package com.example.note.EditNote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.database.entities.Reminder

class ChecklistAdapter(private val onClick: (Reminder) -> Unit):
    ListAdapter<Reminder, ChecklistAdapter.ChecklistViewHolder>(RemindersDiffCallback) {

    class ChecklistViewHolder(itemView: View, val onClick: (Reminder) -> Unit):
        RecyclerView.ViewHolder(itemView) {

        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val dateTime: TextView = itemView.findViewById(R.id.notificationDatetime)
        private val notificationButton: ImageView = itemView.findViewById(R.id.setNotification)
        private var currentReminder: Reminder? = null

        init {
            itemView.setOnClickListener{ currentReminder?.let{ onClick(it) } }
        }

        fun bind(reminder: Reminder) {
            with(reminder) {
                currentReminder = this
                checkbox.text = body
                dateTime.text = time
                checkbox.isChecked = reminderOff
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkbox_item_container, parent, false)
        return ChecklistViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object RemindersDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.body == newItem.body &&
                oldItem.time == newItem.time &&
                oldItem.reminderOff == newItem.reminderOff
    }
}