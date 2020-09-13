package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.util.inflate

class HashTagRecyclerView() : RecyclerView.Adapter<HashTagRecyclerView.ViewHolder>() {

    private var data: List<String>? = emptyList()

    fun initData(data: List<String>?){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_hashtag))

    override fun getItemCount() : Int {
        if (!data.isNullOrEmpty()) {
            return data!!.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data!!.get(position))
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTag : TextView = itemView.findViewById(R.id.txt_hash_tag)
        fun bind(item: String){
            txtTag.text = "#"+item
        }
    }
}