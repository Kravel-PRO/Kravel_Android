package com.kravelteam.kravel_android.ui.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.setRound
import kotlinx.android.synthetic.main.fragment_home.rv_home_photo_review
import kotlinx.android.synthetic.main.fragment_map_info.*


class MapInfoFragment() : BottomSheetDialogFragment() {

    private val photoAdapter : PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() }
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_map_info,null)
        dialog?.setContentView(contentView)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // initPlaceInfo()
        initPhotoRecycler()
        //initMap()

    }
//    private fun initPlaceInfo() {
//        val tmapData : TMapData = TMapData()
//        GlideApp.with(img_bottom_place).load("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg").into(img_bottom_place)
//        // Round값 물어보기
//        img_bottom_place.setRound(10.dpToPx().toFloat())
//        txt_bottom_title.text = markerItem.name
//        txt_bottom_map_address1.text = tmapData.convertGpsToAddress(markerItem.latitude, markerItem.longitude)
//        txt_bottom_map_address2.text = tmapData.convertGpsToAddress(markerItem.latitude, markerItem.longitude)
//
//        rv_map_hashtag.apply {
//            adapter = hashtagAdapter
//            addItemDecoration(HorizontalItemDecorator(4))
//        }
//
//        hashtagAdapter.initData(
//            listOf( HashTagData("호텔델루나"),
//                HashTagData("아이유"),
//                HashTagData("여진구")
//                )
//        )
//    }
//    private fun initMap() {
//        val tmap = view?.findViewById<FrameLayout>(R.id.ll_bottom_map)
//        val tmapView = TMapView(context)
//        tmapView.setSKTMapApiKey(getString(R.string.tmap_api_key))
//        tmapView.setCompassMode(true)
//        tmapView.zoomLevel = 15
//        tmapView.setLanguage(TMapView.LANGUAGE_KOREAN)
//        tmapView.mapType = TMapView.MAPTYPE_STANDARD
//        tmapView.setTrackingMode(false)
//        tmapView.setSightVisible(false)
//        tmapView.setIconVisibility(false)
//        tmap?.addView(tmapView)
//
//        tmapView.setCenterPoint(markerItem.latitude,markerItem.longitude)
//        tmapView.addMarkerItem("test",markerItem)
//
//    }
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