package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible

class PhotoReviewRecyclerview() : RecyclerView.Adapter<PhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<PhotoResponse> = emptyList()

    fun initData(data: List<PhotoResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if(data.size > 6){
            6
        } else {
            data.size
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == 5){
            holder.bind2(data[position])
        } else {
            holder.bind(data[position])
        }

        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_photo_review))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_photo_review)
        private val view: View = itemView.findViewById(R.id.view_img_background)
        private val txtMore : TextView = itemView.findViewById(R.id.txt_photo_more)
        fun bind(item: PhotoResponse){
            GlideApp.with(itemView).load(item.img).into(img)
            txtMore.setGone()
            view.setGone()
        }
        fun bind2(item: PhotoResponse){
            GlideApp.with(itemView).load(item.img).into(img)
            txtMore.setVisible()
            view.setVisible()
        }
    }
}