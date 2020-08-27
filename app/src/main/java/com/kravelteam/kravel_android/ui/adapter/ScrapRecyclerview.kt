package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.response.ScrapResponse
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound

class ScrapRecyclerview(): RecyclerView.Adapter<ScrapRecyclerview.ViewHolder>(){

    private var data: List<ScrapResponse> = emptyList()

    fun initData(data: List<ScrapResponse>){
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

        fun bind(item: ScrapResponse){
            GlideApp.with(itemView).load(item.imgUrl).into(img)
            img.setRound(10.dpToPx().toFloat())
            txtPlaceName.text = item.placeName
        }
    }
}