package com.voice.licentaclientkotlin.usecases

import android.util.Log
import com.voice.licentaclientkotlin.entities.dataclasses.conversation.ConversationData
import com.voice.licentaclientkotlin.entities.dataclasses.conversation.Sentence
import com.voice.licentaclientkotlin.entities.dataclasses.medication.HourAndMinutes
import com.voice.licentaclientkotlin.entities.dataclasses.medication.Medication
import com.voice.licentaclientkotlin.usecases.tree.Node
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Storage private constructor() {

    var list: ArrayList<Medication>
    var userCode: Int
    lateinit var username: String
    lateinit var conversationData: ConversationData
    lateinit var root: Node
    var positivityList: ArrayList<Sentence>
    var historyOfPositivity: HashMap<LocalDateTime, ArrayList<Sentence>>

    init {
        list = ArrayList()
        userCode = -1
        positivityList = ArrayList()
        historyOfPositivity = HashMap()
    }

    private object HOLDER {
        val INSTANCE = Storage()
    }

    companion object {
        val instance: Storage by lazy { HOLDER.INSTANCE }
    }

    fun saveList(list: List<Medication>) {
        Log.e("Storage", "saving list..")
        this.list = ArrayList(list)
        this.list.forEach { Log.e("Storage", it.toString()) }
    }

    private fun getCalendar(hourAndMinutes: HourAndMinutes): Calendar {

        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourAndMinutes.hour)
            set(Calendar.MINUTE, hourAndMinutes.minutes)
            set(Calendar.SECOND, 0)
        }
    }

    fun getNextAlarm(): JSONObject? {
        val currentCalendar = Calendar.getInstance()
        for (item: Medication in list) {
            val sortedByHourList = item.treatment.listHoursAndMinutes.sortedWith(compareBy({ it.hour }, { it.minutes }))
            for (hourAndMinutes in sortedByHourList) {
                val calendar = getCalendar(hourAndMinutes)
                //TODO check the interval of treatment
                if (calendar.after(currentCalendar)) {
                    return JSONObject().put("title", item.drug.name)
                        .put(
                            "hourAndMinutes",
                            "" + hourAndMinutes.hour + ":" + hourAndMinutes.minutes
                        )
                }
            }
        }
        return null
    }

    fun setTaken(id: Int) {
        val hourAndMinutes = getHourAndMinutesById(id)
        hourAndMinutes?.let {
            it.taken = LocalDateTime.now().toString()
        }
        Log.e("Storage", hourAndMinutes.toString())
    }

    fun getHourAndMinutesById(id: Int): HourAndMinutes? {
        var hourAndMinutes: HourAndMinutes? = null
        list.forEach { med ->
            med.treatment.listHoursAndMinutes.forEach {
                if (it.id == id) {
                    hourAndMinutes = it
                }
            }
        }
        return hourAndMinutes
    }
}