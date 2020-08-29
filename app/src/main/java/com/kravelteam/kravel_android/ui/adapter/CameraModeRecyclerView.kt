package com.kravelteam.kravel_android.ui.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.util.inflate

class CameraModeRecyclerView() : RecyclerView.Adapter<CameraModeRecyclerView.ViewHolder>() {

    private var data: List<String> = emptyList()

    fun initData(data: List<String>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_rv_camera_mode))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnMode : Button = view.findViewById(R.id.btn_camera_mode)
        fun bind(item: String){
            itemView.setOnClickListener {
                btnMode.setTextColor(Color.parseColor("#121212"))
                btnMode.setBackgroundResource(R.drawable.btn_camera_clicked)
            }
            btnMode.text = item
        }
    }
}