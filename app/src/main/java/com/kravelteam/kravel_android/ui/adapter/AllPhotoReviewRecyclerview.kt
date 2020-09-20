package com.kravelteam.kravel_android.ui.adapter

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible
import com.kravelteam.kravel_android.util.startActivity
import kotlinx.android.synthetic.main.dialog_logout.view.*

class AllPhotoReviewRecyclerview(
    val checkReview: String,
    val onLike: (Boolean, Int, () -> Unit) -> Unit,
    val onDelete: (Int, () -> Unit) -> Unit,
    val onAllDelete: () -> Unit
): RecyclerView.Adapter<AllPhotoReviewRecyclerview.ViewHolder>(){

    private var data = mutableListOf<PhotoReviewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_all_photo_review))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    fun initData(data: MutableList<PhotoReviewData>){
        this.data = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val img : ImageView = itemView.findViewById(R.id.img_my_photo_review_photo)
        private val txtPlaceName : TextView = itemView.findViewById(R.id.txt_my_photo_review_place_name)
        private val txtYear : TextView = itemView.findViewById(R.id.txt_my_photo_review_year)
        private val txtLikeCount : TextView = itemView.findViewById(R.id.txt_my_photo_review_like_count)
        private val imgLike : ImageView = itemView.findViewById(R.id.img_my_photo_like)
        private val imgDelete : ImageView = itemView.findViewById(R.id.img_my_photo_delete)

        fun bind(item: PhotoReviewData){
            GlideApp.with(itemView).load(item.imageUrl).into(img)
            txtYear.text = item.createdDate
            txtLikeCount.text = item.likeCount.toString()
            if(checkReview == "my"){
                imgDelete.setVisible()
                imgLike.setGone()
                txtPlaceName.setVisible()
                txtPlaceName.text = item.place.title

                imgDelete.setOnDebounceClickListener {
                    if(data.size == 1){
                        onAllDelete()
                    }
                    onDelete(item.reviewId) {
                        AnimationUtils.loadAnimation(KravelApplication.GlobalApp,R.anim.push_left_out)
                            .run { itemView.startAnimation(this) }
                        Handler().postDelayed({
                            data.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            notifyDataSetChanged()
                        },400)
                    }
                }
            } else if(checkReview == "default"){
                imgDelete.setGone()
                imgLike.setVisible()
                txtPlaceName.setGone()
                imgLike.isSelected = item.like
                imgLike.setOnDebounceClickListener {
                    onLike(imgLike.isSelected,item.reviewId) {
                        var now = item.likeCount
                        if(it.isSelected) {
                            now--
                            it.isSelected = false
                            txtLikeCount.text = (now).toString()
                        } else {
                            now++
                            it.isSelected = true
                            txtLikeCount.text = (now).toString()
                        }
                    }
                }
            }
        }
    }
}