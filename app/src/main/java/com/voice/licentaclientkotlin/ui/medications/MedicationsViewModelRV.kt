package com.voice.licentaclientkotlin.ui.medications

class MedicationsViewModelRV {
    private var id = 0
    private var title : String
    private var timesADay : String
    private var daysInterval : String
    private var treatmentDuration : String


    constructor(title: String, timesADay: String, daysInterval: String, treatmentDuration: String) {
        this.title = title
        this.timesADay = timesADay
        this.daysInterval = daysInterval
        this.treatmentDuration = treatmentDuration
        this.id++
    }

    fun  getTitle(): String {
        return this.title
    }

    fun getTimesADay(): String {
        return this.timesADay
    }

    fun getDaysInterval() : String {
        return this.daysInterval
    }

    fun getTreatment() :String {
        return this.treatmentDuration
    }

}