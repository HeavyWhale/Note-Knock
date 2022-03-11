package com.example.note.EditNote

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.Notification.Notification
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.entities.Reminder

class ChecklistAdapter(private val onClick: (Reminder) -> Unit):
    ListAdapter<Reminder, ChecklistAdapter.ChecklistViewHolder>(RemindersDiffCallback) {

    class ChecklistViewHolder(itemView: View, val onClick: (Reminder) -> Unit):
        RecyclerView.ViewHolder(itemView) {

        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val dateTime: TextView = itemView.findViewById(R.id.notificationDatetime)
        private val notificationButton: ImageView = itemView.findViewById(R.id.setNotification)
        private val checkboxBody: TextView = itemView.findViewById(R.id.checkboxBody)
        private val saveChangeButton: ImageView = itemView.findViewById(R.id.saveChecklistChange)
        private var currentReminder: Reminder? = null

        init {
            itemView.setOnClickListener{ currentReminder?.let { onClick(it) } }
            val context: Context = notificationButton.context
            notificationButton.setOnClickListener{
                val intent = Intent(context, Notification::class.java)
                context.startActivity(intent)
            }
            saveChangeButton.setOnClickListener{
                updateReminder()
            }
            /*
            itemView.setOnFocusChangeListener{ _, hasFocus ->
                if(hasFocus) {
                    Log.d("Reminder", "Get focus.")
                }
                else{
                    updateReminder()
                }
            }
            */
        }

        fun bind(reminder: Reminder) {
            with(reminder) {
                currentReminder = this
                checkboxBody.text = body
                dateTime.text = time
                checkbox.isChecked = reminderOff
            }
        }

        private fun updateReminder(){
            Log.d("Reminder", "Update reminder.")
            currentReminder?.let {
                Model.updateReminder(
                    reminderID = it.id,
                    body = checkboxBody.text.toString(),
                    time = dateTime.text.toString(),
                    noteID = it.noteID,
                    reminderOff = checkbox.isChecked
                )
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
