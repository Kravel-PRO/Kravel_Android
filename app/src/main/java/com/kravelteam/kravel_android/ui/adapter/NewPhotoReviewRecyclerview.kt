package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.request.ReviewLikeBody
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.data.response.PhotoReviewResponse
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.*
import org.koin.core.context.GlobalContext
import org.koin.experimental.property.inject
import org.w3c.dom.Text
import timber.log.Timber

class NewPhotoReviewRecyclerview() :  RecyclerView.Adapter<NewPhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<PhotoReviewData> = emptyList()
    fun initData(data: List<PhotoReviewData>){
        this.data = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(v:View, data: PhotoReviewData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position],listener)
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_rv_photo_review))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_rv_photo)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_rv_photo_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_rv_photo_tag)
        private val txtPhotoLike : TextView = itemView.findViewById(R.id.txt_rv_photo_like)
        private val imgPhotoLike : ImageView = itemView.findViewById(R.id.img_rv_photo_like)
        fun bind(item: PhotoReviewData, listener: OnItemClickListener?){
            GlideApp.with(itemView).load(item.imageUrl).into(img)
            img.setRound(10.dpToPx().toFloat())
            txtPlace.text = item.place.title
            if(item.like) {
                GlideApp.with(itemView).load(R.drawable.btn_like).into(imgPhotoLike)
            } else {
                GlideApp.with(itemView).load(R.drawable.btn_like_unclick).into(imgPhotoLike)
            }

            txtPhotoLike.text = item.likeCount.toString()

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }

        }
    }
}
