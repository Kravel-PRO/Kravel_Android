package com.kravelteam.kravel_android.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.NearPlaceData
import com.kravelteam.kravel_android.ui.adapter.MapPlaceRecyclerview
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.LocationTrackingMode.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_user.*
import timber.log.Timber


class MapViewFragment : Fragment(),OnMapReadyCallback{
    private lateinit var locationOverlay: LocationOverlay
    private lateinit var locationSource : FusedLocationSource
    private lateinit var mapView : MapView
    private var trackingmode : Boolean = false
    private lateinit var naverMap : NaverMap

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


        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlterMessageNoGPS()
        }

        initRecycler()
        initMap()
        togglebtn_gps.setOnDebounceClickListener {
            if(!trackingmode) {
                trackingmode = true
                naverMap.locationTrackingMode = Follow

            } else {
                trackingmode = false
                naverMap.locationTrackingMode = NoFollow
            }
        }



    }

//    private fun initBottomSheet(markerItem: TMapMarkerItem) {
//        val bottomSheetDialogFragment = MapInfoFragment(markerItem)
//        fragmentManager?.let { bottomSheetDialogFragment.show(it, bottomSheetDialogFragment.tag) }
//    }

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
    /*
    핸드폰 gps 가 꺼져있을 시 , gps를 키기위한 함수
     */
    private fun buildAlterMessageNoGPS() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("해당 기기에 gps가 꺼져있어 위치정보를 가져올 수 없습니다. gps를 사용하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
    private fun initMap() {
        val marker = com.naver.maps.map.overlay.Marker()
        marker.position = LatLng(37.496502, 126.956100)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
           if(!locationSource.isActivated) {
               naverMap.locationTrackingMode = None
           }
           return
       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = Follow
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        naverMap.addOnCameraChangeListener{reason,animated ->
            if(reason == CameraUpdate.REASON_GESTURE) {
                togglebtn_gps.isChecked = false
                trackingmode = false
            }
        }

    }

}


