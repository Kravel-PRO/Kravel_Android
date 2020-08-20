package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.util.inflate

class HashTagRecyclerView() : RecyclerView.Adapter<HashTagRecyclerView.ViewHolder>() {

    private var data: List<HashTagData> = emptyList()

    fun initData(data: List<HashTagData>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_hashtag))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTag : TextView = itemView.findViewById(R.id.txt_hash_tag)
        fun bind(item: HashTagData){
            txtTag.text = "#"+item.tag
        }
    }
}