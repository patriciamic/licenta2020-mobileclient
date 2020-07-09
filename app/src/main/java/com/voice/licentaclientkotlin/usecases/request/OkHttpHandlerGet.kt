package com.voice.licentaclientkotlin.usecases.request

import android.util.Log
import okhttp3.*
import java.io.IOException

class OkHttpHandlerGet {
    private var url : String
    private var requestCode : Int
    lateinit var  listener : ReceiverResponseListener
    constructor(url: String, requestCode: Int) {
        this.url = url
        this.requestCode = requestCode
    }

    fun processRequest() {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    call.cancel()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val myResponse = response.body!!.string()
                    listener.onReceivedGETMessage(myResponse, requestCode)
                }
            })
        } catch (e: Exception) {
            Log.e("Err", "" + e.message)
        }
    }

    interface ReceiverResponseListener {
        fun onReceivedGETMessage(response: String?, requestCode: Int)
    }
}