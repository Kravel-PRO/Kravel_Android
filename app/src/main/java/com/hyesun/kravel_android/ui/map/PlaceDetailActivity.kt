package com.hyesun.kravel_android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.GlideApp
import com.hyesun.kravel_android.common.HorizontalItemDecorator
import com.hyesun.kravel_android.data.mock.HashTagData
import com.hyesun.kravel_android.ui.adapter.HashTagRecyclerView
import com.hyesun.kravel_android.util.dpToPx
import com.hyesun.kravel_android.util.setRound
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.fragment_map_info.*

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