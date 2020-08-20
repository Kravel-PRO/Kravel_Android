package com.kravelteam.kravel_android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import kotlinx.android.synthetic.main.activity_place_detail.*

class PlaceDetailActivity : AppCompatActivity() {
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        initHashTag()
    }
    private fun initHashTag() {
        rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        hashtagAdapter.initData(
            listOf( HashTagData("호텔델루나"),
                HashTagData("아이유"),
                HashTagData("여진구")
            )
        )
    }
}