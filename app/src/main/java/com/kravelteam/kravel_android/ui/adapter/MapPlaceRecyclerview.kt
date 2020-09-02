package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.NearPlaceData
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound


class MapPlaceRecyclerview() : RecyclerView.Adapter<MapPlaceRecyclerview.ViewHolder>() {

    private var data: List<PlaceContentResponse> = emptyList()

    fun initData(data: List<PlaceContentResponse>){
        this.data = data
        notifyDataSetChanged()
    }


    interface OnItemClickListener{
        fun onItemClick(v:View, data: PlaceContentResponse, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_map_near_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position],listener)
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_map_near)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_map_near_title)
        private val txtAddress : TextView = itemView.findViewById(R.id.txt_map_near_address)
        private val clContent : ConstraintLayout = itemView.findViewById(R.id.cl_map_near)
        private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
        private val rvHashTag : RecyclerView = itemView.findViewById(R.id.rv_map_near_hashtag)

        fun bind(item: PlaceContentResponse,listener: OnItemClickListener?){
            if(!item.imageUrl.isNullOrBlank()) {
                GlideApp.with(itemView).load(item.imageUrl).into(img)
            }
            clContent.setRound(5.dpToPx().toFloat())
            txtPlace.text = item.title
            txtAddress.text = item.location
            initHashTag(item.tags)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
        private fun initHashTag(hashTagData: Array<String>?) {
            rvHashTag.apply {
                adapter = hashtagAdapter
                addItemDecoration(HorizontalItemDecorator(4))
            }

            hashtagAdapter.initData(hashTagData)
        }
    }
}