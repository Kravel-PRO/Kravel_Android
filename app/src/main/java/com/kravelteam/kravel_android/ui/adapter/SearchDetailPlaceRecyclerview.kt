package com.kravelteam.kravel_android.ui.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.ui.map.PlaceDetailActivity
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound

class SearchDetailPlaceRecyclerview : RecyclerView.Adapter<SearchDetailPlaceRecyclerview.ViewHolder>() {

    private var data = mutableListOf<DetailPlaceResponse>()

    fun initData(data: MutableList<DetailPlaceResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    fun addData(data: MutableList<DetailPlaceResponse>){
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_search_detail_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_search_detail_place)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_search_detail_place)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_search_detail_place_tag)

        fun bind(item: DetailPlaceResponse){
            GlideApp.with(itemView).load(item.imageUrl).into(img)
            img.setRound(10.dpToPx().toFloat())
            txtPlace.text = item.title
            var tags = ""
            item.tags?.split(",")?.forEach {
                tags += "#$it "
            }
            txtTag.text = tags
            itemView.setOnDebounceClickListener {
                Intent(GlobalApp, PlaceDetailActivity::class.java).apply {
                    putExtra("placeId",item.placeId)
                    putExtra("mode","home")
                }.run {
                    GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }
    }
}