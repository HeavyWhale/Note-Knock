package com.example.note.Notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.note.EXTRA_REMINDER_ID
import com.example.note.database.Model
import com.example.note.databinding.SetNotificationBinding
import java.util.*
import kotlin.properties.Delegates

class Notification : AppCompatActivity() {
    private lateinit var binding : SetNotificationBinding
    private var currentReminderID by Delegates.notNull<Int>()
    var notificationTime:String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        // use binding to bind the view
        binding = SetNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentReminderID = intent.getIntExtra(EXTRA_REMINDER_ID, 0)

        createNotificationChannel()
        binding.submitBottom.setOnClickListener {
            scheduleNotification()
            notificationTime = getTimeInString()
            Model.updateReminderTimeByID(currentReminderID, notificationTime)
        }
        
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, SendNotification::class.java)
        val title = binding.TitleET.text.toString()
        val message = binding.messageET.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title : " + title + "\nMessage : " + message + "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("OK") { _, _ ->
                //set what would happen when positive button is clicked
                finish()
            }
            .show()
    }

    fun getTimeInString(): String {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year
        return "$year.$month.$day $hour:$minute"
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
