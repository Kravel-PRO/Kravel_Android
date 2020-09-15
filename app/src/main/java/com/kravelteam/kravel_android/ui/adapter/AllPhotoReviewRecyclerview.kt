package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible

class AllPhotoReviewRecyclerview(val checkReview: String, val onLike: (Boolean,Int) -> Unit): RecyclerView.Adapter<AllPhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<PhotoReviewData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_my_photo_review))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    fun initData(data: List<PhotoReviewData>){
        this.data = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val img : ImageView = itemView.findViewById(R.id.img_my_photo_review_photo)
        private val txtPlaceName : TextView = itemView.findViewById(R.id.txt_my_photo_review_place_name)
        private val txtYear : TextView = itemView.findViewById(R.id.txt_my_photo_review_year)
        private val txtLikeCount : TextView = itemView.findViewById(R.id.txt_my_photo_review_like_count)
        private val imgLike : ImageView = itemView.findViewById(R.id.img_my_photo_like)

        fun bind(item: PhotoReviewData){
            GlideApp.with(itemView).load(item.imageUrl).into(img)
            txtYear.text = item.createdDate
            txtLikeCount.text = item.likeCount.toString()
            if(checkReview == "my"){
                imgLike.setGone()
                txtPlaceName.setVisible()
                txtPlaceName.text = item.place.title
            } else if(checkReview == "default"){
                imgLike.setVisible()
                txtPlaceName.setGone()
                imgLike.isSelected = item.like
                imgLike.setOnDebounceClickListener {
                    if(it.isSelected) {
                        it.isSelected = false
                        txtLikeCount.text = (item.likeCount-1).toString()
                    } else {
                        it.isSelected = true
                        txtLikeCount.text = (item.likeCount+1).toString()
                    }
                    onLike(imgLike.isSelected,item.reviewId)
                }
            }
        }
    }
}