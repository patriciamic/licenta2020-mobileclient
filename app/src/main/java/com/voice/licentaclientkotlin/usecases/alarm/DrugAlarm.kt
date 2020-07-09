package com.voice.licentaclientkotlin.usecases.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.voice.licentaclientkotlin.entities.dataclasses.medication.HourAndMinutes
import com.voice.licentaclientkotlin.entities.dataclasses.medication.Medication
import com.voice.licentaclientkotlin.usecases.AppSystem
import java.util.*

class DrugAlarm {
    companion object {

        fun launchAlarm(context: Context, list: List<Medication>) {
            for (item: Medication in list) {
                for (hourAndMinutes: HourAndMinutes in item.treatment.listHoursAndMinutes) {
                    // TODO check the interval of treatment - Medication
                    val calendar = getCalendar(hourAndMinutes)
                    if (Calendar.getInstance().before(calendar)) {
                        launchAlarm(context, hourAndMinutes.id, item.drug.name, calendar)
                    }
                }
            }
        }

        private fun getCalendar(hourAndMinutes: HourAndMinutes): Calendar {
            return Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hourAndMinutes.hour)
                set(Calendar.MINUTE, hourAndMinutes.minutes)
                set(Calendar.SECOND, 0)
            }
        }

        fun launchAlarm(
            context: Context,
            pendingIntentId: Int,
            drugName: String,
            calendar: Calendar
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val alarmIntent =
                Intent(context, MedicationBroadcastReceiver::class.java).let { intent ->
                    intent.putExtra(AppSystem.AlarmMedExtra, "$drugName-$pendingIntentId")
                    PendingIntent.getBroadcast(
                        context,
                        pendingIntentId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }
    }

}