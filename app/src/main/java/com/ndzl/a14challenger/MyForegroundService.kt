package com.ndzl.a14challenger


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat


class MyForegroundService : Service() {
    // ... other service code
    override fun onBind(intent: Intent): IBinder {
        //TODO("Return the communication channel to the service.")
        return throw UnsupportedOperationException("Not yet implemented")
    }

    //@RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        // ... your service logic
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            "your_channel_id",
            "Channel Name",
            NotificationManager.IMPORTANCE_LOW // Or higher
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, "your_channel_id")
            .setContentTitle("Service Running")
            .setContentText("Your service is running in the foreground.")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this,MainActivity2::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()


    }
    }