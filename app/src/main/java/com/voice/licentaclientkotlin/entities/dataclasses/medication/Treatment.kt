package com.voice.licentaclientkotlin.entities.dataclasses.medication

data class Treatment(val id: Int, val timesADay: Int, val daysInterval: Int, val start: String, val stop: String, val listHoursAndMinutes: List<HourAndMinutes>)