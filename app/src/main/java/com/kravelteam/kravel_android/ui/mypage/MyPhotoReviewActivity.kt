package com.kravelteam.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.MyPhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_my_photo_review.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class MyPhotoReviewActivity : AppCompatActivity() {

    private lateinit var myPhotoReviewAdapter: MyPhotoReviewRecyclerview
    private val networkManager : NetworkManager by inject()

    private var checkReview = ""
    private var checkPart = ""
    private var id : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_photo_review)

        checkReview = intent.getStringExtra("review")!!
        initRecycler()

        if(checkReview == "my") { //내 포토리뷰

            initGetMyPhotoReview()
            txt_my_photo_review_title.text = "내 포토리뷰"

        } else if(checkReview == "default"){ //포토리뷰

            checkPart = intent.getStringExtra("part")!!
            id = intent.getIntExtra("id",0)

            txt_my_photo_review_title.text = "포토리뷰"

            when (checkPart) {
                "celeb" -> { //셀럽 리뷰 불러오기

                    initGetCelebPhotoReview()

                }
                "media" -> { // 미디어 리뷰 불러오기

                    initGetMediaPhotoReview()

                }
                "place" -> { //장소 리뷰 불러오기

                    initGetPlacePhotoReview()

                }
            }

        }

        initSetting()
    }

    private fun initSetting(){
        img_my_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        myPhotoReviewAdapter = MyPhotoReviewRecyclerview(checkReview)
        rv_my_photo_review.apply {
            adapter = myPhotoReviewAdapter
        }
    }

    private fun initGetMyPhotoReview(){
        networkManager.requestMyPhotoReviews().safeEnqueue(
            onSuccess = {
                myPhotoReviewAdapter.initData(it.data.result.content)
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
    }

    private fun initGetCelebPhotoReview(){
//        networkManager.getCelebPhotoReview(id).safeEnqueue(
//            onSuccess = {
//                myPhotoReviewAdapter.initData(it.data.result)
//            },
//            onFailure = {
//                toast("실패")
//            },
//            onError = {
//                networkErrorToast()
//            }
//        )
    }

    private fun initGetMediaPhotoReview(){
//        networkManager.requestMediaPhotoReview(id).safeEnqueue(
//            onSuccess = {
//                myPhotoReviewAdapter.initData(it.data.result)
//            },
//            onFailure = {
//                toast("실패")
//            },
//            onError = {
//                networkErrorToast()
//            }
//        )
    }

    private fun initGetPlacePhotoReview(){
//        networkManager.getPlaceReview(id).safeEnqueue(
//            onSuccess = {
//                myPhotoReviewAdapter.initData(it.data.result)
//            },
//            onFailure = {
//                toast("실패")
//            },
//            onError = {
//                networkErrorToast()
//            }
//        )
    }
}