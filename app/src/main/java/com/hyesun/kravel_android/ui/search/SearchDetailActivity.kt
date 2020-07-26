package com.hyesun.kravel_android.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.common.HorizontalItemDecorator
import com.hyesun.kravel_android.common.VerticalItemDecorator
import com.hyesun.kravel_android.data.response.DetailPlaceResponse
import com.hyesun.kravel_android.ui.adapter.SearchDetailPlaceRecyclerview
import kotlinx.android.synthetic.main.activity_search_detail.*

class SearchDetailActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)

        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        GlideApp.with(this).load("https://6.vikiplatform.com/image/a11230e2d98d4a73825a4c10c8c6feb0.jpg?x=b&a=0x0&s=460x268&e=t&f=t&cb=1").into(img_search_detail_title)
    }

    private fun initRecycler(){
        rv_search_detail_place.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        placeAdapter.initData(
            listOf(
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag")),
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag")),
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag")),
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag")),
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag")),
                DetailPlaceResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"))
            )
        )
    }
}