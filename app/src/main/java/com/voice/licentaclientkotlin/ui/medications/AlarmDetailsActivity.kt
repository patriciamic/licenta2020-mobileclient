package com.voice.licentaclientkotlin.ui.medications

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voice.licentaclientkotlin.R
import com.voice.licentaclientkotlin.usecases.AppSystem
import com.voice.licentaclientkotlin.usecases.Storage
import com.voice.licentaclientkotlin.usecases.alarm.DrugAlarm
import kotlinx.android.synthetic.main.activity_alarm_details.*
import java.util.*


class AlarmDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private var medId: Int = 0
    private lateinit var medTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_details)

        button_take_pill.setOnClickListener(this)
        button_skip_pill.setOnClickListener(this)

        intent.extras?.let {
            val extra = it.getString(AppSystem.AlarmMedExtra)
            medTitle = extra!!.split("-")[0]
            medId = extra.split("-")[1].toInt()
            Log.e("Med AlarmDetails", "$medTitle, $medId")
            tv_title.text = medTitle;
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_take_pill -> {
                Storage.instance.setTaken(medId)
                finish()
            }
            R.id.button_skip_pill -> {
                snooze()
                Toast.makeText(this, "Skipped for " + 2 +" minutes", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun snooze() {
        Storage.instance.getHourAndMinutesById(medId)?.let {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 2)
            DrugAlarm.launchAlarm(this, medId, medTitle, calendar)
        }
    }
}
