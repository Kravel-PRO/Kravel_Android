package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.response.MyPhotoReviewResponse
import com.kravelteam.kravel_android.util.inflate

class MyPhotoReviewRecyclerview (): RecyclerView.Adapter<MyPhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<MyPhotoReviewResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_my_photo_review))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    fun initData(data: List<MyPhotoReviewResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val img : ImageView = itemView.findViewById(R.id.img_my_photo_review_photo)
        private val txtPlaceName : TextView = itemView.findViewById(R.id.txt_my_photo_review_place_name)
        private val txtYear : TextView = itemView.findViewById(R.id.txt_my_photo_review_year)
        private val txtLike : TextView = itemView.findViewById(R.id.txt_my_photo_review_like_count)

        fun bind(item: MyPhotoReviewResponse){
            GlideApp.with(itemView).load(item.imgUrl).into(img)
            txtPlaceName.text = item.placeName
            txtYear.text = item.year
            txtLike.text = item.like.toString()
        }
    }
}