package com.example.notification

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notification.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManager? = null
    private var CHANNELID = "channel_id"
    private var ACTION_SNOOZE = "snooze"

    private lateinit var countDownTimer: CountDownTimer

    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, NotificationScreen::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        //register channel kedalam sistem
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        createNotificationChannel(CHANNELID, "Countdown", "ini merupakan deskripsi")

        binding.btnStart.setOnClickListener {
            countDownTimer.start()
        }
        countDownTimer = object : CountDownTimer(6000, 1000) {
            override fun onTick(p0: Long) {
                binding.timer.text = getString(R.string.time_remaining, p0 / 1000)
            }

            override fun onFinish() {
                displayNotification()
            }

        }

    }

    fun displayNotification() {
        val snoozeIntent = Intent(this, NotificationScreen::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        val intent = Intent(this, NotificationScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(this, CHANNELID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Alert")
            .setContentText("Your timer ends")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_baseline_snooze_24, getString(R.string.snooze),
                snoozePendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }


    }

    fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        // validasi notif akan dibuat juga version SDK 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}