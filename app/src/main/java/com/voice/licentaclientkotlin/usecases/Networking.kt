package com.voice.licentaclientkotlin.usecases

class Networking {
    companion object {
        private const val baseURL = "http://192.168.0.105:3000"
        const val message = "$baseURL/message"
        const val signIn = "$baseURL/signIn"
        const val conversationStructure = "$baseURL/getConversationStructure"
        const val medication = "$baseURL/getTreatment"
        const val saveConversation = "$baseURL/saveConversation"
    }
}
