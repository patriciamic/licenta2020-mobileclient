package com.voice.licentaclientkotlin.ui.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.voice.licentaclientkotlin.R


class ConversationAdapter(private val myDataset: ArrayList<MyDataModelRV>) :
    RecyclerView.Adapter<ConversationAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvContent: TextView = itemView.findViewById(R.id.tv_content)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = when (viewType) {
            1 -> LayoutInflater.from(parent.context).inflate(
                R.layout.item_conv_recycleview_type_light,
                parent,
                false
            ) as View
            else -> LayoutInflater.from(parent.context).inflate(
                R.layout.item_conv_recycleview_type_dark,
                parent,
                false
            ) as View
        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = myDataset[position]
        holder.tvTitle.text = item.getTitle()
        holder.tvContent.text = item.getContent()
    }

    override fun getItemCount() = myDataset.size

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            1
        } else {
            2
        }
    }

}