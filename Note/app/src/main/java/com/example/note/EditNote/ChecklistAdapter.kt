package com.example.note.EditNote

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.note.EXTRA_REMINDER_ID
import com.example.note.Notification.Notification
import com.example.note.R
import com.example.note.database.Model
import com.example.note.database.entities.Reminder

class ChecklistAdapter
    : ListAdapter<Reminder, ChecklistAdapter.ChecklistViewHolder>(RemindersDiffCallback) {

    class ChecklistViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val dateTime: TextView = itemView.findViewById(R.id.notificationDatetime)
        private val notificationButton: ImageView = itemView.findViewById(R.id.moreChecklistActions)
        private val checkboxBody: TextView = itemView.findViewById(R.id.checkboxBody)
        private var currentReminder: Reminder? = null

        init {
            val context: Context = notificationButton.context
            notificationButton.setOnClickListener{
                val intent = Intent(context, Notification::class.java).apply {
                    currentReminder?.let { reminder -> putExtra(EXTRA_REMINDER_ID, reminder.id) }
                }
                context.startActivity(intent)
            }
            checkboxBody.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Log.d("Reminder", "Get focus.")
                } else {
                    updateReminder()
                    Model.editedReminder = true
                }
            }
            checkbox.setOnClickListener{
                Handler(Looper.getMainLooper()).postDelayed({
                    currentReminder?.let { reminder -> Model.deleteReminder(reminder.id) }
                }, 1000)
            }
        }

        fun bind(reminder: Reminder) {
            with(reminder) {
                currentReminder = this
                checkboxBody.text = body
                dateTime.text = time
                checkbox.isChecked = reminderOff
            }
        }

        private fun updateReminder() {
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
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemsCount(): Int {
        return this.itemCount
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
