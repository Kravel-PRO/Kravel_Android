package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.mock.MapNearPlaceData
import com.kravelteam.kravel_android.data.mock.PlaceInformationData
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound

class MapNearPlaceRecyclerview() : RecyclerView.Adapter<MapNearPlaceRecyclerview.ViewHolder>() {

    private var data: List<MapNearPlaceData> = emptyList()

    fun initData(data: List<MapNearPlaceData>){
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_map_rv_near_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_rv_map_near_place)
        private val view : View = itemView.findViewById(R.id.img_rv_map_near_view)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_rv_map_near_place)

        fun bind(item: MapNearPlaceData){
            GlideApp.with(itemView).load(item.imgPlace).into(img)
            img.setRound(56.dpToPx().toFloat())
            view.setRound(56.dpToPx().toFloat())
            txtPlace.text = item.txtPlace
        }
    }
}