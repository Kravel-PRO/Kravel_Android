package com.kravelteam.kravel_android.ui.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.data.response.MediaResponse
import com.kravelteam.kravel_android.ui.search.SearchDetailActivity
import com.kravelteam.kravel_android.util.inflate

class DramaRecyclerview() : RecyclerView.Adapter<DramaRecyclerview.ViewHolder>() {

    private var data: List<MediaResponse> = emptyList()

    fun initData(data: List<MediaResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_celeb_title))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_celeb_profile)
        private val txtName : TextView = itemView.findViewById(R.id.txt_celeb_name)
        private val txtYear : TextView = itemView.findViewById(R.id.txt_drama_year)

        fun bind(item: MediaResponse){
            itemView.setOnDebounceClickListener {
                Intent(KravelApplication.GlobalApp,SearchDetailActivity::class.java).apply {
                    putExtra("id",item.mediaId)
                    putExtra("part","media")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { KravelApplication.GlobalApp.startActivity(this) }
            }
            GlideApp.with(itemView).load(item.imageUrl).apply(RequestOptions.circleCropTransform()).into(img)
            txtName.text = item.title
            txtYear.visibility = View.VISIBLE
            txtYear.text = item.year
        }
    }
}