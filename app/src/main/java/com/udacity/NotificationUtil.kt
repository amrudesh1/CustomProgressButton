package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat


val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(message: String, context: Context) {

    val contentIntent = Intent(context, MainActivity::class.java)


    val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
                context.getString(R.string.NOTIFICATION_CHANNEL_ID),
                context.getString(R.string.download_channel_name),
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_DEFAULT
        )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(true)
                }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Download Completed"
        createNotificationChannel(notificationChannel)
    }


    val builder = NotificationCompat.Builder(context,
            context.getString(R.string.NOTIFICATION_CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentIntent(contentPendingIntent)
            .setContentTitle(context.getString(R.string.NOTIFICATION_TITLE))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        builder.setChannelId(context.getString(R.string.NOTIFICATION_CHANNEL_ID))
    }

    Log.i("TAGNOTIFICATION", "SEND NOTIFCICATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notify(NOTIFICATION_ID, builder.build())
    } else {
        builder.build()
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
