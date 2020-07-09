package com.voice.licentaclientkotlin.ui.medications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voice.licentaclientkotlin.R


class MedicationsAdapter(private val myDataset: ArrayList<MedicationsViewModelRV>) :
    RecyclerView.Adapter<MedicationsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvTimesADay: TextView = itemView.findViewById(R.id.tv_times_a_day)
        var tvDaysInterval: TextView = itemView.findViewById(R.id.tv_days_interval)
        var tvTreatmentDuration: TextView = itemView.findViewById(R.id.tv_treatment_duration)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_med_recycleview, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = myDataset[position]
        holder.tvTitle.text = item.getTitle()
        holder.tvTimesADay.text = item.getTimesADay()
        holder.tvDaysInterval.text = item.getDaysInterval()
        holder.tvTreatmentDuration.text = item.getTreatment()
    }

    override fun getItemCount() = myDataset.size



}