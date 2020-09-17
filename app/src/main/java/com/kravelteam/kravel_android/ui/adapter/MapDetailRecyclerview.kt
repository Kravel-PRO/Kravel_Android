package com.kravelteam.kravel_android.ui.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.data.response.MediaResponse
import com.kravelteam.kravel_android.ui.search.SearchDetailActivity
import com.kravelteam.kravel_android.util.inflate
import kotlinx.android.synthetic.main.activity_place_detail.*
import java.lang.reflect.Array
import java.net.URL
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class MapDetailRecyclerview() : RecyclerView.Adapter<MapDetailRecyclerview.ViewHolder>() {

    private var data: List<String> = emptyList()

    fun initData(data: List<String>){
        this.data = data

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_map_detail_image))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_rv_map_detail)

        fun bind(item: String){
            GlideApp.with(img).load(item).into(img)

        }
    }
}