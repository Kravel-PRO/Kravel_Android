package com.hyesun.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.common.HorizontalItemDecorator
import com.hyesun.kravel_android.data.mock.HashTagData
import com.hyesun.kravel_android.data.mock.NearPlaceData
import com.hyesun.kravel_android.data.response.DetailPlaceResponse
import com.hyesun.kravel_android.util.dpToPx
import com.hyesun.kravel_android.util.inflate
import com.hyesun.kravel_android.util.setRound
import kotlinx.android.synthetic.main.activity_place_detail.*


class MapPlaceRecyclerview() : RecyclerView.Adapter<MapPlaceRecyclerview.ViewHolder>() {

    private var data: List<NearPlaceData> = emptyList()

    fun initData(data: List<NearPlaceData>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_map_near_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_map_near)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_map_near_title)
        private val txtAddress : TextView = itemView.findViewById(R.id.txt_map_near_address)
        private val clContent : ConstraintLayout = itemView.findViewById(R.id.cl_map_near)
        private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
        private val rvHashTag : RecyclerView = itemView.findViewById(R.id.rv_map_near_hashtag)

        fun bind(item: NearPlaceData){
            GlideApp.with(itemView).load(item.img).into(img)
            clContent.setRound(5.dpToPx().toFloat())
            txtPlace.text = item.placeTitle
            txtAddress.text = item.address
            initHashTag(item.tag)
        }
        private fun initHashTag(hashTagData: List<HashTagData>) {
            rvHashTag.apply {
                adapter = hashtagAdapter
                addItemDecoration(HorizontalItemDecorator(4))
            }

            hashtagAdapter.initData(hashTagData)
        }
    }
}