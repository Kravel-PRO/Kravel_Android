package com.kravelteam.kravel_android.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.ui.adapter.NearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NewPhotoReviewRecyclerview
import kotlinx.android.synthetic.main.activity_near_place.*

class NearPlaceActivity : AppCompatActivity() {

    private val placeAdapter : NearPlaceRecyclerview by lazy { NearPlaceRecyclerview() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_place)
        initRecycler()
        img_near_place_back.setOnDebounceClickListener {
            finish()
        }
    }
    private fun initRecycler() {
        rv_near_place.apply {
            adapter = placeAdapter
            addItemDecoration(VerticalItemDecorator(16))
        }

        placeAdapter.initData(
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