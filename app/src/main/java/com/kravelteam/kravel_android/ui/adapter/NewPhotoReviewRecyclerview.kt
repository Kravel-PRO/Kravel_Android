package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.util.*
import org.w3c.dom.Text

class NewPhotoReviewRecyclerview() : RecyclerView.Adapter<NewPhotoReviewRecyclerview.ViewHolder>(){

    private var data: List<NewPhotoReview> = emptyList()

    fun initData(data: List<NewPhotoReview>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
       return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_rv_photo_review))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_rv_photo)
        private val txtPlace : TextView = itemView.findViewById(R.id.txt_rv_photo_title)
        private val txtTag : TextView = itemView.findViewById(R.id.txt_rv_photo_tag)
        fun bind(item: NewPhotoReview){
            GlideApp.with(itemView).load(item.img).into(img)
            img.setRound(10.dpToPx().toFloat())
            txtPlace.text = item.place
            var str : String =""
            for(i in 0..item.tag.size) {
                str.plus( "#" + item.tag.get(i))
                if(i!=0 && i!=item.tag.size) {
                    str.plus(" ")
                }
            }
            txtTag.text = str
        }
    }
}