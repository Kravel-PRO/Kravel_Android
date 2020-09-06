package com.kravelteam.kravel_android.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.AddressData
import com.kravelteam.kravel_android.util.inflate

class AddressRecyclerview(val onFinish: (address: String) -> Unit) : RecyclerView.Adapter<AddressRecyclerview.ViewHolder>(){

    private var data: List<AddressData> = emptyList()

    fun initData(data: List<AddressData>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_address))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val txtPlaceName :TextView = view.findViewById(R.id.txt_place_name)
        private val txtPlaceAddress : TextView = view.findViewById(R.id.txt_place_address)

        fun bind(item: AddressData){
            txtPlaceName.text = item.place_name
            txtPlaceAddress.text = item.address_name

            itemView.setOnDebounceClickListener {
                onFinish(item.place_name)
            }
        }
    }

}