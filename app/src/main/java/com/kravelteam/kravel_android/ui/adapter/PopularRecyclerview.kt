package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.inflate
import com.kravelteam.kravel_android.util.setRound
import timber.log.Timber

class PopularRecyclerview() : RecyclerView.Adapter<PopularRecyclerview.ViewHolder>() {


    private var data : List<PlaceContentResponse> = listOf()

    fun initData(data: List<PlaceContentResponse>) {
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
        = ViewHolder(parent.inflate(R.layout.item_home_popular_place))

    override fun getItemCount(): Int {
        return if(data.size>5) {
            5
        } else {
            data.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(data[position],listener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_home_popular)
        private val view : View = itemView.findViewById(R.id.gradient)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_home_popular_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_home_popular_tag)
        private val txtPhohoNum : TextView = itemView.findViewById(R.id.txt_home_popular_photo)

        fun bind(item: PlaceContentResponse, listener: OnItemClickListener?) {
            if(!item.imageUrl.isNullOrBlank()) {
                GlideApp.with(itemView).load(item.imageUrl!!).into(img)
            }
            img.setRound(20.dpToPx().toFloat())
            view.setRound(20.dpToPx().toFloat())
            txtPlace.text = item.title

            var str : String = ""
            if(!item.tags.isNullOrEmpty()) {
                val tag = item.tags.split(",")
                for(i in 0 until tag.size) {
                    Timber.e("size::"+i)
                    str += "#"+tag.get(i)+" "
                }
                txtTag.text = str
            }
            txtPhohoNum.text = item.reviewCount.toString()

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