package com.voice.licentaclientkotlin.usecases.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.voice.licentaclientkotlin.R
import com.voice.licentaclientkotlin.ui.medications.AlarmDetailsActivity
import com.voice.licentaclientkotlin.usecases.AppSystem

class MedicationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try{
            val extra = intent?.getStringExtra(AppSystem.AlarmMedExtra)

            val title = extra!!.split("-")[0]
            val id = extra.split("-")[1].toInt()
            Log.e("Med BR", "$title, $id")

            launchNotification(context, extra, title, id)
        }catch(e: Exception){
            Log.e("MedicationBroadcastReceiver", "Error: " + e.message)
        }

    }

    private fun launchNotification(
        context: Context?,
        extra: String?,
        title: String,
        id: Int
    ) {
        val alarmIntent = Intent(context, AlarmDetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        alarmIntent.putExtra(AppSystem.AlarmMedExtra, extra)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, 0)

        val builder = NotificationCompat.Builder(context!!, AppSystem.AlarmMedChannelId)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("Medication $title")
            .setContentText("Tap to take your pill")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }
}