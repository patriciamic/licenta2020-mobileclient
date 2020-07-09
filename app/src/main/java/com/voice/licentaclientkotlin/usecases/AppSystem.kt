package com.voice.licentaclientkotlin.usecases

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.voice.licentaclientkotlin.R

class AppSystem {
    companion object {
        const val AlarmMedExtra = "alarmmedextra"
        const val AlarmMedChannelId = "alarmmedchannelid"
        fun  createNotificationChannel(
            context: Context,
            channelName: String,
            descriptionName: String,
            channelId: String
        ) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionName
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


    }

}