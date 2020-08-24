package com.kravelteam.kravel_android.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NewPhotoReviewRecyclerview
import kotlinx.android.synthetic.main.activity_photo_review.*
import kotlinx.android.synthetic.main.fragment_home.*

class PhotoReviewActivity : AppCompatActivity() {
    private val photoAdapter : NewPhotoReviewRecyclerview by lazy { NewPhotoReviewRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_review)
        initPhotoReivew()

        img_photo_review_back.setOnClickListener {
            finish()
        }
    }
    private fun initPhotoReivew() {
        rv_photo_review.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(18))
            addItemDecoration(HorizontalItemDecorator(8))
        }

        photoAdapter.initData(
            listOf(
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오")),
                NewPhotoReview("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나",
                    arrayOf<String>("호텔델루나","여진구","피오"))

            )
        )
    }
}