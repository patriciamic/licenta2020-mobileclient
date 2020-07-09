package com.voice.licentaclientkotlin.ui.conversation

class MyDataModelRV {
    private var id = 0
    private var title : String
    private var content : String

    constructor(title: String, content: String) {
        this.title = title
        this.content = content
        this.id++
    }

    fun  getTitle(): String {
        return this.title
    }

    fun  getContent(): String {
        return this.content
    }

}