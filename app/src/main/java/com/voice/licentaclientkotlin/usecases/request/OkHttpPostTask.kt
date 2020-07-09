package com.voice.licentaclientkotlin.usecases.request

import android.os.AsyncTask
import android.util.Log
import com.google.gson.JsonObject
import okhttp3.*
import java.util.*
import kotlin.collections.HashMap

class OkHttpPostTask : AsyncTask<String, Void, String>() {

    lateinit var listener: ReceiverResponseListener
    lateinit var text: String
    private val client = OkHttpClient()
    lateinit var url: String
    var requestCode = 0
    lateinit var params : HashMap<String, String>

    override fun doInBackground(vararg parameters: String?): String {
        try {
            params["from"] = "android"
            val builder = FormBody.Builder()
            for ((key, value) in params) {
                builder.add(key, value)
            }
            val formBody: RequestBody = builder.build()
            val request = Request.Builder()
                .url(url)
                //.addHeader("token", "@ndroid")
                .post(formBody)
                .build()
            val response: Response = client.newCall(request).execute()
            listener.onReceivedMessage(response.body!!.string(), requestCode)
        } catch (e: Exception) {
            Log.e("ok http post task ERR", " err " + e.message)
            listener.onReceivedMessage(e.message, requestCode)
        }

        return "done"
    }

    interface ReceiverResponseListener {
        fun onReceivedMessage(message: String?, requestCode: Int)
    }
}