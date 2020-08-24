package com.kravelteam.kravel_android.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.ui.adapter.NearRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PopularRecyclerview
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private val popularAdapter : PopularRecyclerview by lazy { PopularRecyclerview() }
    private val photoAdapter : PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() }
    private val nearAdapter : NearRecyclerview by lazy { NearRecyclerview() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        initPopularRecycler()
        initPhotoRecycler()
        initNearRecycler()

    }
    private fun init() {
        txt_home_near_place_more.setOnClickListener {
            startActivity(Intent(context, NearPlaceActivity::class.java))
        }
    }
    private fun initNearRecycler() {
        rv_near_place.apply {
            adapter = nearAdapter
            addItemDecoration(HorizontalItemDecorator(16))
        }

        nearAdapter.initData(
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
    private fun initPopularRecycler() {
        rv_popular_place.apply {
            adapter = popularAdapter
            addItemDecoration(VerticalItemDecorator(12))
        }

        popularAdapter.initData(
            listOf(
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),100),
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),10),
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),30),
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),50),
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),50),
                PopularPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호델 델루나", arrayListOf("#tag"),70)
            )
        )

    }
    private fun initPhotoRecycler() {
        rv_home_photo_review.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

        photoAdapter.initData(
            listOf( PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg")
            )
        )
    }


}
