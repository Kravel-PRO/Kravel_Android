package com.kravelteam.kravel_android.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.SearchDetailPlaceRecyclerview
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_search_detail.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SearchDetailActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    private val photoAdapter: PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() }
    private val networkManager : NetworkManager by inject()

    private var id : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)

        id = intent.getIntExtra("id",0)
        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        GlideApp.with(this).load("https://6.vikiplatform.com/image/a11230e2d98d4a73825a4c10c8c6feb0.jpg?x=b&a=0x0&s=460x268&e=t&f=t&cb=1").into(img_search_detail_title)
        img_search_detail_back.setOnDebounceClickListener {
            finish()
        }
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


        networkManager.getCelebPhotoReview(id).safeEnqueue(
            onSuccess = {
                rv_search_detail_photo_review.apply{
                    adapter = photoAdapter
                    addItemDecoration(HorizontalItemDecorator(4))
                    addItemDecoration(VerticalItemDecorator(4))
                }
                if(!it.data.result.reviews.isNullOrEmpty()) {
                    photoAdapter.initData(it.data.result!!.reviews)
                }
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                Timber.e("$it")
            }
        )
    }
}