package com.voice.licentaclientkotlin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.voice.licentaclientkotlin.MainActivity
import com.voice.licentaclientkotlin.R
import com.voice.licentaclientkotlin.usecases.Storage
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject

class HomeFragment : Fragment(), MainActivity.DataChangedListener {

    private lateinit var animation: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        animation = view.findViewById(R.id.animation_typing)
        animation.setMinAndMaxProgress(0.1f, 0.9f)
        animation.playAnimation()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.listener = this
        updateDrugView()
    }

    override fun onPause() {
        updateDrugView()
        super.onPause()
    }

    private fun updateDrugView() {
        val nextAlarm: JSONObject? = Storage.instance.getNextAlarm()
        if (nextAlarm == null) {
            tv_title.text = "RELAX"
            tv_subtitle.text = "No medications to take"
        } else {
            tv_title.text = nextAlarm.getString("title")
            tv_subtitle.text = nextAlarm.getString("hourAndMinutes")
        }
    }

    override fun onDataChanged() {
        runOnUiThread {
            updateDrugView()
        }
    }
}
