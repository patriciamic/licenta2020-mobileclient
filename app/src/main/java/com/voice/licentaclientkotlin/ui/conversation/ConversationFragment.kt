package com.voice.licentaclientkotlin.ui.conversation

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.voice.licentaclientkotlin.R
import com.voice.licentaclientkotlin.entities.dataclasses.conversation.Sentence
import com.voice.licentaclientkotlin.usecases.Networking
import com.voice.licentaclientkotlin.usecases.Storage
import com.voice.licentaclientkotlin.usecases.request.OkHttpPostTask
import com.voice.licentaclientkotlin.usecases.tree.Node
import kotlinx.android.synthetic.main.fragment_conversation.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ConversationFragment : Fragment(), View.OnClickListener,
    OkHttpPostTask.ReceiverResponseListener {

    private val reqCodeSpeechInput = 100
    private val LogTag = "Conversation"
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var list: ArrayList<MyDataModelRV>
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var textRetry: String
    private lateinit var root: Node
    private lateinit var currentNode: Node
    private lateinit var currentSentence: String
    var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_conversation, container, false)

        Storage.instance.positivityList = ArrayList()
        root = Storage.instance.root
        currentNode = root

        list = ArrayList()
        list.add(MyDataModelRV(root.getGoal()!!.question, getCurrentHourAndMinutesFormatted() + ""))

        textToSpeech = TextToSpeech(context,
            OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.language = Locale.UK
                }
            })
        initRecycleView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animation_typing.visibility = View.GONE
        btn_voice.setOnClickListener(this)
        btn_send_message.setOnClickListener(this)
        btn_send_message.visibility = View.GONE

        et_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (et_message.text.isNotEmpty()) {
                    btn_send_message.visibility = View.VISIBLE
                } else {
                    btn_send_message.visibility = View.GONE
                }
            }
        })

    }

    private fun initRecycleView(view: View) {
        viewManager = LinearLayoutManager(context)
        viewAdapter = ConversationAdapter(list)
        recyclerView = view.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.adapter = viewAdapter
        if (list.size > 1) {
            recyclerView.scrollToPosition(list.size - 1)
        }
    }

    override fun onDestroy() {
        Storage.instance.historyOfPositivity[LocalDateTime.now()] = Storage.instance.positivityList
        sendConversation()

        super.onDestroy()
    }

    private fun sendConversation() {
        val task = OkHttpPostTask()
        task.url = Networking.saveConversation + "/" + Storage.instance.userCode
        task.requestCode = 2
        task.listener = this
        val params = HashMap<String, String>()

        params["localDateTime"] = LocalDateTime.now().toString()
        val jsonArray = JSONArray()
        Storage.instance.positivityList.forEach { item ->
            val jsonObject = JSONObject()
            jsonObject.put("question", item.question)
            jsonObject.put("answer", item.answer)
            jsonObject.put("type", item.type)
            jsonArray.put(jsonObject)
        }

        params["conversation"] = jsonArray.toString()
        task.params = params
        task.execute()
        Thread.sleep(2000)
    }

    private fun getCurrentHourAndMinutesFormatted(): String? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return current.format(formatter)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_voice -> promptSpeechInput();
            btn_send_message -> {
                if (et_message.text.isNotEmpty()) {
                    list.add(
                        MyDataModelRV(
                            et_message.text.toString(),
                            getCurrentHourAndMinutesFormatted() + ""
                        )
                    );
                    recyclerView.scrollToPosition(list.size - 1)
                    sendMessage(et_message.text.toString(), false)
                    currentSentence = et_message.text.toString()
                    animation_typing.visibility = View.VISIBLE
                    animation_typing.playAnimation()
                    et_message.setText("")
                }
            }
        }
    }

    private fun sendMessage(message: String, retry: Boolean) {

         calendar = Calendar.getInstance();
        Log.e("Send Message ", calendar.timeInMillis.toString());
        val task = OkHttpPostTask()
        task.listener = this

        task.url = Networking.message
        task.requestCode = 1;
        val params = HashMap<String, String>()

        if (retry) {
            params["text"] = textRetry;
        } else {
            textRetry = message
            params["text"] = message
        }

        params["id"] = "android-" + LocalDateTime.now().toString();
        task.params = params
        task.execute()
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt)
        )
        try {
            startActivityForResult(intent, reqCodeSpeechInput)
        } catch (a: ActivityNotFoundException) {
            Log.e(LogTag, "err activity not found " + a.message);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            reqCodeSpeechInput -> {
                data?.let {
                    if (resultCode == RESULT_OK) {
                        val result = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        Log.e(LogTag, result[0])
                        et_message.setText(result[0])
                    }
                }
            }
        }
    }

    override fun onReceivedMessage(message: String?, requestCode: Int) {
        runOnUiThread {
            Log.e("Received Message ", "dif milis " + (Calendar.getInstance().timeInMillis - calendar.timeInMillis))
            Log.e(LogTag, "reqcode $requestCode")
            when (requestCode) {
                1 -> handleConversation(message)
                2 -> {
                    message?.let { Log.e(LogTag, message) }
                }
                else -> {
                    Log.e(LogTag, "else on received message")
                }
            }
        }
    }

    private fun handleConversation(message: String?) {
        if (message != null) {
            if (message.contains("Failed to connect")) {
                Snackbar.make(view!!, "Check Connection and Try Again!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            } else {
                if (message.contains("timeout")) {
                    Snackbar.make(view!!, "Timeout..", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
                    sendMessage(message, true);
                } else {
                    Log.e(LogTag, "Message : $message");

                    if (currentNode.getChildren() != null && currentNode.getChildren()!!.isNotEmpty()) {
                        val temp = currentNode.getChildren()
                        if (message.toUpperCase(Locale.ROOT).contains("POSITIVE")) {
                            val nodeFound = temp!!.find { it?.getGoal()!!.type == "positive" }
                            nodeFound?.let{
                                currentNode = nodeFound
                            }
                        }

                        if (message.toUpperCase(Locale.ROOT).contains("NEUTRAL")) {
                            val nodeFound = temp!!.find { it?.getGoal()!!.type == "neutral" }
                            nodeFound?.let{
                                currentNode = nodeFound
                            }
                        }

                        if (message.toUpperCase(Locale.ROOT).contains("NEGATIVE")) {
                            val nodeFound = temp!!.find { it?.getGoal()!!.type == "negative" }
                            nodeFound?.let{
                                currentNode = nodeFound
                            }
                        }
                        addMessageToConversation(currentNode.getGoal()!!.question)
                        Storage.instance.positivityList.add(
                            Sentence(
                                currentNode.getParent()!!.getGoal()!!.question,
                                currentSentence,
                                message
                            )
                        )

                    } else {
                        addMessageToConversation("Conversation saved.");
                        runOnUiThread {
                            btn_send_message.visibility = View.GONE
                            btn_voice.visibility = View.GONE
                            et_message.visibility = View.GONE
                        }

                        Storage.instance.positivityList.add(
                            Sentence(
                                currentNode.getGoal()!!.question,
                                currentSentence,
                                message
                            )
                        )
                    }

                }
            }
            animation_typing.visibility = View.GONE
        }
    }

    private fun addMessageToConversation(message: String) {
        list.add(MyDataModelRV(message, getCurrentHourAndMinutesFormatted() + ""))
        if (list.size > 0) {
            recyclerView.scrollToPosition(list.size - 1)
        }
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

}
