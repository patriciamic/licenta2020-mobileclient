package com.voice.licentaclientkotlin.ui.medications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.voice.licentaclientkotlin.R
import com.voice.licentaclientkotlin.entities.dataclasses.medication.Medication
import com.voice.licentaclientkotlin.usecases.Storage

class MedicationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var list: ArrayList<MedicationsViewModelRV>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_medications, container, false)

        list = ArrayList()

        for (x: Medication in Storage.instance.list) {
            list.add(
                MedicationsViewModelRV(
                    x.drug.name,
                    "${x.treatment.timesADay} x per day",
                    "Every ${x.treatment.daysInterval} day",
                    x.treatment.start +" - " + x.treatment.stop
                )
            )
        }

        initRecycleView(view)

        return view
    }




    private fun initRecycleView(view: View) {

        viewManager = LinearLayoutManager(context)
        viewAdapter = MedicationsAdapter(list)
        recyclerView = view.findViewById<RecyclerView>(R.id.rv_medications).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.adapter = viewAdapter

    }
}
