package com.kravelteam.kravel_android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.PlaceInformationData
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.fragment_map_info.*

class PlaceDetailActivity : AppCompatActivity() , OnMapReadyCallback {
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    private val photoAdapter : PhotoReviewRecyclerview by lazy {PhotoReviewRecyclerview()}
    private lateinit var placeInfo : PlaceInformationData
    private lateinit var naverMap: NaverMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        placeInfo = intent.getParcelableExtra("data") as PlaceInformationData
        img_map_detail_arrow.setOnClickListener {
            finish()
        }
        initSetting()
    }
    private fun initSetting() {
        txt_map_detail_title.text = placeInfo.placeName
        txt_map_detail_address.text = placeInfo.placeAddress
        txt_map_detail_address2.text = placeInfo.placeAddress
        GlideApp.with(applicationContext).load(placeInfo.placeImg).into(img_map_detail_place)

        initMap()
        initHashTag()
        initPhotoRecycler()
    }
    private fun initMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.place_detail_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.place_detail_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun initHashTag() {
        rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        hashtagAdapter.initData(placeInfo.placeTag)
    }
    private fun initPhotoRecycler() {
        rv_map_detail_photo.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

        photoAdapter.initData(placeInfo.placePhotoReview)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val marker = Marker()
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
        naverMap.moveCamera(CameraUpdate.scrollTo(placeInfo.marker))
        marker.position = placeInfo.marker
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

    }
}