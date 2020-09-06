package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.response.MyScrapData
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound

class ScrapRecyclerview(): RecyclerView.Adapter<ScrapRecyclerview.ViewHolder>(){

    private var data: List<MyScrapData> = emptyList()

    fun initData(data: List<MyScrapData>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_scrap))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val img: ImageView = itemView.findViewById(R.id.img_scrap_place)
        private val txtPlaceName: TextView = itemView.findViewById(R.id.txt_scrap_place)

        fun bind(item: MyScrapData){
            GlideApp.with(itemView).load(item.imageUrl).into(img)
            img.setRound(10.dpToPx().toFloat())
            txtPlaceName.text = item.title
        }
    }
}