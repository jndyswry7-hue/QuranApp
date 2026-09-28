package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "quran_daily_wird_channel"
    private const val CHANNEL_NAME = "تذكير الورد اليومي والمراجعة"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تنبيهات يومية لتذكير المستخدم بورد تلاوة القرآن الكريم ومواعيد المراجعة"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showDailyWirdReminder(context: Context, title: String, message: String) {
        try {
            createNotificationChannel(context)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            // Checked via try-catch for permission handling
            notificationManager.notify(1001, builder.build())
        } catch (_: SecurityException) {
            // Permission not yet granted, gracefully ignore
        } catch (_: Exception) {}
    }
}
