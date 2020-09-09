package com.kravelteam.kravel_android.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NewPhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PopularRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.setGone
import kotlinx.android.synthetic.main.activity_photo_review.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class PhotoReviewActivity : AppCompatActivity() {
    private val networkManager : NetworkManager by inject()
    private val photoAdapter : NewPhotoReviewRecyclerview by lazy { NewPhotoReviewRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_review)
        initPhotoReivew()

        img_photo_review_back.setOnDebounceClickListener {
            finish()
        }
    }
    private fun initPhotoReivew() {
        networkManager.getPhotoReview( page= 0, size = 20 , sort ="reviewLikes,desc").safeEnqueue (
            onSuccess = {
//                rv_home_photo_review.apply {
//                    adapter = photoAdapter
//                    addItemDecoration(VerticalItemDecorator(4))
//                    addItemDecoration(HorizontalItemDecorator(4))
//                }

//                photoAdapter.initData(it.data.result.content)
                if(it.data.result.content.isNullOrEmpty()) {
                    txt_home_photo1.setGone()
                    txt_home_photo2.setGone()
                }
            },
            onFailure = {
                Timber.e("실패")
            },
            onError = {
                networkErrorToast()
            }
        )

        photoAdapter.initData(
            listOf(
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayListOf<String>("호텔델루나","여진구","피오")
                ),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayListOf<String>("호텔델루나","여진구","피오")
                ),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayListOf<String>("호텔델루나","여진구","피오")
                ),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayListOf<String>("호텔델루나","여진구","피오")
                ),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayListOf<String>("호텔델루나","여진구","피오")
                )

            )
        )
    }
}