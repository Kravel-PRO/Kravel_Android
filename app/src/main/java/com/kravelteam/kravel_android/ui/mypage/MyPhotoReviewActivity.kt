package com.kravelteam.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.MyPhotoReviewResponse
import com.kravelteam.kravel_android.ui.adapter.MyPhotoReviewRecyclerview
import kotlinx.android.synthetic.main.activity_my_photo_review.*

class MyPhotoReviewActivity : AppCompatActivity() {

    private val myPhotoReviewAdapter: MyPhotoReviewRecyclerview by lazy { MyPhotoReviewRecyclerview() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_photo_review)

        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        img_my_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        rv_my_photo_review.apply {
            adapter = myPhotoReviewAdapter
        }
        myPhotoReviewAdapter.initData(
            listOf(
                MyPhotoReviewResponse(
                    0,
                    "호텔 세느장",
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "2020.08.20",
                    20
                ),
                MyPhotoReviewResponse(
                    0,
                    "호텔 세느장",
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "2020.08.20",
                    20
                ),
                MyPhotoReviewResponse(
                    0,
                    "호텔 세느장",
                    "https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg",
                    "2020.08.20",
                    20
                )
            )
        )
    }
}