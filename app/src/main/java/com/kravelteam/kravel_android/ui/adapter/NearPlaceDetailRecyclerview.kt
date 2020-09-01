package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.util.*

class NearPlaceDetailRecyclerview() : RecyclerView.Adapter<NearPlaceDetailRecyclerview.ViewHolder>(){

    private var data: List<PlaceContentResponse> = emptyList()

    fun initData(data: List<PlaceContentResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
       return data.size
    }
    interface OnItemClickListener{
        fun onItemClick(v:View, data: PlaceContentResponse, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(data[position],listener)
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_rv_near_place))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_rv_near)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_rv_near_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_rv_near_tag)
        fun bind(item: PlaceContentResponse, listener: OnItemClickListener?){
            if(!item.imageUrl.isNullOrBlank()) {
                GlideApp.with(itemView).load(item.imageUrl).into(img)
            }
            img.setRound(5.dpToPx().toFloat())
            txtPlace.text = item.title
            var str : String = ""
            for(i in 0 until item.tags!!.size) {
                str = str+"#"+item.tags!!.get(i)

                if(i!=item.tags!!.size-1) {
                    str = str+" "
                }
            }
            txtTag.text = str


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
