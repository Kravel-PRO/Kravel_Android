package com.kravelteam.kravel_android.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.data.response.PhotoReviewData
import com.kravelteam.kravel_android.ui.home.PhotoReviewActivity
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible
import org.koin.core.context.GlobalContext

class PhotoReviewRecyclerview() : RecyclerView.Adapter<PhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<PhotoReviewData> = emptyList()

    fun initData(data: List<PhotoReviewData>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == data.size){
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
        fun bind(item: PhotoReviewData){
            if(!item.imageURl.isNullOrEmpty()) {
                GlideApp.with(itemView).load(item.imageURl).into(img)
            }
            txtMore.setGone()
            view.setGone()
        }
        fun bind2(item: PhotoReviewData){
            if(!item.imageURl.isNullOrEmpty()) {
                GlideApp.with(itemView).load(item.imageURl).into(img)
            }
            txtMore.setVisible()
            view.setVisible()
            img.setOnDebounceClickListener {
                Intent(GlobalApp, PhotoReviewActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { GlobalApp.startActivity(this)
                }
            }
        }
    }
}
