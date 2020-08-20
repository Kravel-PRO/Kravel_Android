package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.common.SearchWord
import com.kravelteam.kravel_android.util.inflate

class SearchWordRecyclerview() : RecyclerView.Adapter<SearchWordRecyclerview.ViewHolder>(){

    private var data = mutableListOf<SearchWord>()

    fun initData(data: List<SearchWord>){
        this.data = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        =ViewHolder(parent.inflate(R.layout.item_search_word))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val txtWord: TextView = itemView.findViewById(R.id.txt_search_word)
        private val imgDelete: ImageView = itemView.findViewById(R.id.img_search_word_delete)

        fun bind(item: SearchWord){
            txtWord.text = item.word
            imgDelete.setOnDebounceClickListener {
                data.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }
}