package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val ALARM_CHANNEL_ID = "yawmi_alarm_loud_v2"
    private const val ALARM_CHANNEL_NAME = "تنبيهات ورنين يومي (Yawmi Alarms)"
    private const val ALARM_CHANNEL_DESC = "تنبيهات ورنين فوري للمهام والملاحظات المجدولة بأعلى أولوية"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                ALARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = ALARM_CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 400, 800, 400, 800)
                enableLights(true)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(
        context: Context,
        taskId: Long,
        title: String,
        content: String
    ) {
        createNotificationChannel(context)

        // 1. Forcefully trigger loud alarm sound & vibration
        AlarmPlayer.playAlarm(context)

        val notificationId = taskId.toInt().coerceAtLeast(1)

        // Open App Intent
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TASK_ID", taskId)
            putExtra("FROM_ALARM", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop Alarm Action Intent
        val stopIntent = Intent(context, AlarmStopReceiver::class.java).apply {
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 500000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isAr = java.util.Locale.getDefault().language == "ar"
        val stopLabel = if (isAr) "⏹️ إيقاف الرنين" else "⏹️ Stop Ringing"
        val openLabel = if (isAr) "عرض في التطبيق" else "Open in App"

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOngoing(true) // Keeps notification until user interacts or stops
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(stopPendingIntent)
            .setFullScreenIntent(contentPendingIntent, true) // Heads-up banner even if screen is off
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, stopLabel, stopPendingIntent)
            .addAction(android.R.drawable.ic_menu_view, openLabel, contentPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Shows a high-priority notification in the top Notification Center for an upcoming bill/commitment
     * before or on its due date (e.g. car rent, installment, electricity, etc.)
     */
    fun showUpcomingBillNotification(
        context: Context,
        billId: Long,
        billTitle: String,
        category: String,
        amountFormatted: String,
        dueDateText: String,
        daysLeft: Int,
        playLoudAlarm: Boolean = false
    ) {
        createNotificationChannel(context)

        if (playLoudAlarm) {
            AlarmPlayer.playAlarm(context)
        } else {
            try {
                val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val ringtone = RingtoneManager.getRingtone(context.applicationContext, notificationUri)
                ringtone?.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val notificationId = (300000 + billId.toInt()).coerceAtLeast(300001)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("BILL_ID", billId)
            putExtra("FROM_ALARM", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, AlarmStopReceiver::class.java).apply {
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 500000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isAr = java.util.Locale.getDefault().language == "ar"
        val title = when {
            daysLeft <= 0 -> if (isAr) "🚨 اليوم موعد تسديد: $billTitle" else "🚨 Due Today: $billTitle"
            daysLeft == 1 -> if (isAr) "⚠️ غداً موعد الدفع: $billTitle" else "⚠️ Due Tomorrow: $billTitle"
            else -> if (isAr) "🔔 اقترب موعد الدفع: $billTitle (بعد $daysLeft أيام)" else "🔔 Upcoming Payment: $billTitle (in $daysLeft days)"
        }

        val openLabel = if (isAr) "فتح الالتزامات والتسديد" else "View Commitments"
        val stopLabel = if (isAr) "إغلاق التنبيه" else "Dismiss"

        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(android.R.drawable.ic_menu_view, openLabel, contentPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, stopLabel, stopPendingIntent)

        if (playLoudAlarm) {
            builder.setOngoing(true)
            builder.setFullScreenIntent(contentPendingIntent, true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Test alarm immediately for 5 seconds to verify sound, vibration and notification
     */
    fun testAlarmImmediately(context: Context) {
        showReminderNotification(
            context = context,
            taskId = 999999L,
            title = "🔔 تجربة رنين المنبه الفوري",
            content = "يعمل نظام الرنين والتنبيهات في يومي بكفاءة عالية وبأعلى أولوية في النظام."
        )
    }
}
