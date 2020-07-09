package com.voice.licentaclientkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.voice.licentaclientkotlin.usecases.AppSystem
import com.voice.licentaclientkotlin.usecases.Networking
import com.voice.licentaclientkotlin.usecases.Storage
import com.voice.licentaclientkotlin.usecases.request.OkHttpPostTask
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.HashMap


class LoginActivity : AppCompatActivity(), View.OnClickListener,
    OkHttpPostTask.ReceiverResponseListener {
    private lateinit var etCode: EditText;
    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.btn_check).setOnClickListener(this)
        etCode = findViewById(R.id.et_code)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_check -> {
                if (!etCode.text.isEmpty()) {

                    AppSystem.createNotificationChannel(
                        this,
                        "Alarm medication",
                        "-",
                        AppSystem.AlarmMedChannelId
                    )

                    view = v
                    sendCode(etCode.text.toString())

                } else {
                    Snackbar.make(v, "Input can't be empty", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private fun sendCode(message: String) {
        val task = OkHttpPostTask()
        task.listener = this
        task.url = Networking.signIn
        task.requestCode = 1;
        val params = HashMap<String, String>()
        params["code"] = message
        task.params = params
        task.execute()
    }

    override fun onReceivedMessage(message: String?, requestCode: Int) {
        message?.let {
            Log.e("AAAAAAA", it)
            val jsonObj = JSONObject(message)
            when (jsonObj.get("ok")) {
                true -> {
                    val intent = Intent(view.context, MainActivity::class.java)
                    intent.putExtra("CODE", etCode.text.toString())
                    Storage.instance.userCode = etCode.text.toString().toInt()
                    Storage.instance.username = jsonObj.getString("name")

                    startActivity(intent)
                }
                else -> {
                    runOnUiThread{
                        Snackbar.make(view, "Wrong code", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show()
                        etCode.setText("")
                    }
                }
            }
        }

    }

}
