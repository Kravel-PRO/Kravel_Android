package com.hyesun.kravel_android.ui.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.hyesun.kravel_android.KravelApplication
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.common.setOnDebounceClickListener
import com.hyesun.kravel_android.data.response.CelebResponse
import com.hyesun.kravel_android.ui.search.SearchDetailActivity
import com.hyesun.kravel_android.util.inflate

class CelebRecyclerview(private val drama: Boolean) : RecyclerView.Adapter<CelebRecyclerview.ViewHolder>() {

    private var data: List<CelebResponse> = emptyList()

    fun initData(data: List<CelebResponse>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(parent.inflate(R.layout.item_celeb_title))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img : ImageView = itemView.findViewById(R.id.img_celeb_profile)
        private val txtName : TextView = itemView.findViewById(R.id.txt_celeb_name)
        private val txtYear : TextView = itemView.findViewById(R.id.txt_drama_year)

        fun bind(item: CelebResponse){
            itemView.setOnDebounceClickListener {
                Intent(KravelApplication.GlobalApp,SearchDetailActivity::class.java).apply {
                    putExtra("id","1")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { KravelApplication.GlobalApp.startActivity(this) }
            }
            GlideApp.with(itemView).load(item.img).apply(RequestOptions.circleCropTransform()).into(img)
            txtName.text = item.name
            if(drama) {
                txtYear.visibility = View.VISIBLE
                txtYear.text = item.year.toString()
            } else {
                txtYear.visibility = View.GONE
            }
        }
    }
}