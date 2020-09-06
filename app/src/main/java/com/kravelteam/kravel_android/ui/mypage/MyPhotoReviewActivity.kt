package com.kravelteam.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.MyPhotoReviewData
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.MyPhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_my_photo_review.*
import org.koin.android.ext.android.inject

class MyPhotoReviewActivity : AppCompatActivity() {

    private val myPhotoReviewAdapter: MyPhotoReviewRecyclerview by lazy { MyPhotoReviewRecyclerview() }
    private val networkManager : NetworkManager by inject()

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

        networkManager.requestMyPhotoReviews().safeEnqueue(
            onSuccess = {
                myPhotoReviewAdapter.initData(it.data.result.content)
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                toast("에러")
            }
        )
    }
}