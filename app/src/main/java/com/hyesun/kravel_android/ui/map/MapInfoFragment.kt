package com.hyesun.kravel_android.ui.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.HorizontalItemDecorator
import com.hyesun.kravel_android.common.VerticalItemDecorator
import com.hyesun.kravel_android.data.response.PhotoResponse
import com.hyesun.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.fragment_home.*

class MapInfoFragment : BottomSheetDialogFragment() {

    private val photoAdapter : PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() }
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
        initPlaceInfo()
        initPhotoRecycler()
        initMap()
    }

    private fun initPlaceInfo() {

    }
    private fun initMap() {
        val tmap = view?.findViewById<LinearLayout>(R.id.ll_bottom_map)
        val tmapView = TMapView(context)
        tmapView.setSKTMapApiKey(getString(R.string.tmap_api_key))
        tmapView.setCompassMode(true)
        tmapView.setIconVisibility(true)
        tmapView.zoomLevel = 15
        tmapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tmapView.mapType = TMapView.MAPTYPE_STANDARD
        tmapView.setTrackingMode(true)
        tmapView.setSightVisible(true)
        tmap?.addView(tmapView)

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