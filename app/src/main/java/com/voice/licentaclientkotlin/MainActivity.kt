package com.voice.licentaclientkotlin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.voice.licentaclientkotlin.entities.dataclasses.conversation.ConversationData
import com.voice.licentaclientkotlin.entities.dataclasses.medication.HourAndMinutes
import com.voice.licentaclientkotlin.entities.dataclasses.medication.Medication
import com.voice.licentaclientkotlin.usecases.AppSystem
import com.voice.licentaclientkotlin.usecases.Networking
import com.voice.licentaclientkotlin.usecases.Storage
import com.voice.licentaclientkotlin.usecases.alarm.DrugAlarm
import com.voice.licentaclientkotlin.usecases.alarm.MedicationBroadcastReceiver
import com.voice.licentaclientkotlin.usecases.request.OkHttpHandlerGet
import com.voice.licentaclientkotlin.usecases.request.OkHttpPostTask
import com.voice.licentaclientkotlin.usecases.tree.Node
import com.voice.licentaclientkotlin.usecases.tree.Tree
import java.lang.reflect.Type
import java.util.*


class MainActivity : AppCompatActivity(), OkHttpHandlerGet.ReceiverResponseListener,
    OkHttpPostTask.ReceiverResponseListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        lateinit var listener: DataChangedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "awiee", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_conversation, R.id.nav_medications, R.id.nav_statistics
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (intent.hasExtra("CODE")) {
            navView.getHeaderView(0).findViewById<TextView>(R.id.tv_code).text =
                "My code: " + intent.getStringExtra("CODE")

            navView.getHeaderView(0).findViewById<TextView>(R.id.tv_title).text =
                Storage.instance.username.split(" ")[1] + " " + Storage.instance.username.split(" ")[0]

        }

        getConversationStructure()
        getMedication()
    }

    private fun getMedication() {
        val task = OkHttpPostTask()
        task.url = Networking.medication
        task.requestCode = 1
        val params = HashMap<String, String>()
        params["code"] = Storage.instance.userCode.toString()
        task.params = params
        task.listener = this
        task.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getConversationStructure() {
        val task =
            OkHttpHandlerGet(Networking.conversationStructure + "/" + Storage.instance.userCode, 1)
        task.listener = this
        task.processRequest()
    }

    override fun onReceivedGETMessage(response: String?, requestCode: Int) {
        response?.let {
            val gson = Gson()
            val conversationData = gson.fromJson(it, ConversationData::class.java)
            Log.e("CONV", gson.toJson(conversationData))
            Storage.instance.conversationData = conversationData
            val root = Tree.createTree(conversationData)
            Storage.instance.root = root
        }
    }

    override fun onReceivedMessage(message: String?, requestCode: Int) {
        message?.let {
            val gson = Gson()
            val founderListType: Type = object : TypeToken<ArrayList<Medication?>?>() {}.type
            val list: List<Medication> = gson.fromJson(it, founderListType)
            for (medication in list) {
                medication.treatment.listHoursAndMinutes.forEach { hm ->
                    hm.taken = ""
                }
            }
            Log.e("MEEEED", gson.toJson(list))

            Storage.instance.saveList(list)
            Storage.instance.list.let { DrugAlarm.launchAlarm(this, list) }
            listener.onDataChanged()
        }
    }

    interface DataChangedListener {
        fun onDataChanged()
    }
}
