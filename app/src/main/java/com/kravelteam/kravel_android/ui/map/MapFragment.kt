package com.kravelteam.kravel_android.ui.map

import android.content.pm.PackageManager
import android.graphics.BitmapFactory.decodeResource
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.NearPlaceData
import com.kravelteam.kravel_android.ui.adapter.MapPlaceRecyclerview
import com.skt.Tmap.*
import kotlinx.android.synthetic.main.fragment_map.*
import timber.log.Timber
import java.util.*


class MapFragment : Fragment() {

    lateinit var tmapView : TMapView
    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initMap()
        initRecycler()

        togglebtn_gps.setOnClickListener {
        }
    }

    private fun initBottomSheet(markerItem: TMapMarkerItem) {
        val bottomSheetDialogFragment = MapInfoFragment(markerItem)
        fragmentManager?.let { bottomSheetDialogFragment.show(it, bottomSheetDialogFragment.tag) }
    }

    private fun initRecycler() {
        rv_map_near_place.apply {
            adapter = nearAdapter
            addItemDecoration(HorizontalItemDecorator(16))
        }

        nearAdapter.initData(
            listOf(
                NearPlaceData(
                    "호텔델루나", listOf(HashTagData("아이유"),HashTagData("여진구")),
                    "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
                    "호텔델루나 주소"
                ),
                NearPlaceData(
                    "호텔델루나",
                    listOf(HashTagData("아이유"),HashTagData("여진구")),
                    "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
                    "호텔델루나 주소"
                ),
                NearPlaceData(
                    "호텔델루나",
                    listOf(HashTagData("아이유"),HashTagData("여진구")),
                    "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
                    "호텔델루나 주소"
                ),
                NearPlaceData(
                    "호텔델루나",
                    listOf(HashTagData("아이유"),HashTagData("여진구")),
                    "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
                    "호텔델루나 주소"
                ),
                NearPlaceData(
                    "호텔델루나",
                    listOf(HashTagData("아이유"),HashTagData("여진구")),
                    "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
                    "호텔델루나 주소"
                )

            )
        )
    }

    private fun initMap() {
        val tmap = view?.findViewById<LinearLayout>(R.id.map)
        tmapView = TMapView(context)
        tmapView.setSKTMapApiKey(getString(R.string.tmap_api_key))
        tmapView.setCompassMode(true)
        tmapView.setIconVisibility(true)
        tmapView.zoomLevel = 15
        tmapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tmapView.mapType = TMapView.MAPTYPE_STANDARD
        tmapView.setTrackingMode(true)
        tmapView.setSightVisible(true)
        tmap?.addView(tmapView)

        val tpoint: TMapPoint = TMapPoint(37.570841, 126.985302)
        val tItem: TMapMarkerItem = TMapMarkerItem()

        tItem.tMapPoint = tpoint
        tItem.name = "SKT 타워"
        tItem.visible = TMapMarkerItem.VISIBLE

        val bitmap = decodeResource(context?.resources, R.drawable.place_tag_background)
        tItem.run {
            this.icon = bitmap
            // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
            setPosition(0.5F, 1.0F)
        }         // 마커의 중심점을 하단, 중앙으로 설정

        tmapView.addMarkerItem("test", tItem)
        tmapView.setOnClickListenerCallBack(object : TMapView.OnClickListenerCallback {
            override fun onPressEvent(
                markerlist: ArrayList<TMapMarkerItem>?,
                arraylist1: ArrayList<TMapPOIItem>?,
                tMapPoint: TMapPoint?,
                pointF: PointF?
            ): Boolean {
                if (markerlist!!.isNotEmpty()) {
                    val placeName = markerlist!!.get(0).name
                    val placePoint = markerlist!!.get(0).tMapPoint
                    Timber.e("name ${placeName}")
                    Timber.e("point ${placePoint.toString()}")
                    initBottomSheet(markerlist.get(0))
                }
                return false

            }

            override fun onPressUpEvent(
                p0: ArrayList<TMapMarkerItem>?,
                p1: ArrayList<TMapPOIItem>?,
                p2: TMapPoint?,
                p3: PointF?
            ): Boolean {
                return false
            }

        })
    }

    private fun initGPS() {
         if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(KravelApplication.GlobalApp, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
         {
            ActivityCompat.requestPermissions( requireActivity(),arrayOf ( android.Manifest.permission.ACCESS_FINE_LOCATION ),
                    0 )
             return
         }

        //Timber.d("${tmapgps.location.toString()}")
    }
}

