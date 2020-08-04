package com.hyesun.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.data.mock.PopularPlaceData
import com.hyesun.kravel_android.util.dpToPx
import com.hyesun.kravel_android.util.inflate
import com.hyesun.kravel_android.util.setRound

class PopularRecyclerview() : RecyclerView.Adapter<PopularRecyclerview.ViewHolder>() {

    private var data : List<PopularPlaceData> = listOf()

    fun initData(data: List<PopularPlaceData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(parent.inflate(R.layout.item_home_popular_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(data[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_home_popular)
        private val view : View = itemView.findViewById(R.id.gradient)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_home_popular_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_home_popular_tag)
        private val txtPhohoNum : TextView = itemView.findViewById(R.id.txt_home_popular_photo)

        fun bind(item: PopularPlaceData) {
            GlideApp.with(itemView).load(item.img).into(img)
            img.setRound(20.dpToPx().toFloat())
            view.setRound(20.dpToPx().toFloat())
            txtPlace.text = item.place
            txtTag.text = item.tag[0]
            txtPhohoNum.text = item.photo.toString()

        }
    }

}