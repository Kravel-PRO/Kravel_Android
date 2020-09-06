package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound

class NearPlaceRecyclerview() : RecyclerView.Adapter<NearPlaceRecyclerview.ViewHolder>() {

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
            = ViewHolder(parent.inflate(R.layout.item_home_near_place))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position],listener)
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_near_place)
        private val view : View = itemView.findViewById(R.id.gradient)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_home_near_place_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_home_near_place_tag1)

        fun bind(item: PlaceContentResponse,listener : OnItemClickListener?){
            if(!item.imageUrl.isNullOrEmpty()) {
                GlideApp.with(itemView).load(item.imageUrl).into(img)
            }
            img.setRound(12.dpToPx().toFloat())
            view.setRound(12.dpToPx().toFloat())
            txtPlace.text = item.title

            var str : String = ""
            if(!item.tags.isNullOrEmpty()) {
                for (i in 0 until item.tags!!.size) {
                    str = str + "#" + item.tags!!.get(i)

                    if (i != item.tags!!.size - 1) {
                        str = str + " "
                    }
                }
                txtTag.text = str
            }
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