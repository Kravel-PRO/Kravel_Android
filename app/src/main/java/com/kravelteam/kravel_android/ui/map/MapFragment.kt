package com.kravelteam.kravel_android.ui.map

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory.decodeResource
import android.graphics.PointF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.airbnb.lottie.model.Marker
import com.google.android.gms.location.*
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.NearPlaceData
import com.kravelteam.kravel_android.ui.adapter.MapPlaceRecyclerview
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.core.context.GlobalContext
import org.koin.dsl.koinApplication
import timber.log.Timber
import java.util.*


class MapFragment : Fragment(){
    private lateinit var mapView : MapView
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
    private var checkGPS : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView =requireActivity().findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mLocationRequest = LocationRequest()
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlterMessageNoGPS()
        }

        initRecycler()
        initMap()

        togglebtn_gps.setOnClickListener {
            if(!checkGPS) {
                checkGPS = true
                if(checkPermissionForLocation(KravelApplication.GlobalApp)) {
                    startLocationUpdates()
                }
            } else {
//                tmapView.setSightVisible(false)
//                tmapView.setCompassMode(false)
                checkGPS = false
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
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }

    }
    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        val lat = location.latitude
        val lng = location.longitude
//        tmapView.setLocationPoint(lat!!,lng!!)
//        if(checkGPS) {
//            tmapView.setTrackingMode(true)
//            tmapView.setSightVisible(true)
//            tmapView.setCompassMode(true)
//            tmapView.setLocationPoint(lat!!,lng!!)
//            tmapView.setCenterPoint(lat!!,lng!!)
//        }
        Timber.e("lat : $lat, lng : $lng")

        // You can now create a LatLng Object for use with maps
    }
    private fun initMap() {

        if(checkPermissionForLocation(KravelApplication.GlobalApp)) {
            startLocationUpdates()
        }

        val marker = com.naver.maps.map.overlay.Marker()
        marker.position = LatLng(37.496502, 126.956100)

//        val bitmap = decodeResource(context?.resources, R.drawable.place_tag_background)
//        tItem.run {
//            this.icon = bitmap
//            // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
//            setPosition(0.5F, 1.0F)
//        }         // 마커의 중심점을 하단, 중앙으로 설정
//
//        tmapView.addMarkerItem("test", tItem)
//        tmapView.setOnClickListenerCallBack(object : TMapView.OnClickListenerCallback {
//            override fun onPressEvent(
//                markerlist: ArrayList<TMapMarkerItem>?,
//                arraylist1: ArrayList<TMapPOIItem>?,
//                tMapPoint: TMapPoint?,
//                pointF: PointF?
//            ): Boolean {
//                if (markerlist!!.isNotEmpty()) {
//                    val placeName = markerlist!!.get(0).name
//                    val placePoint = markerlist!!.get(0).tMapPoint
//                    Timber.e("name ${placeName}")
//                    Timber.e("point ${placePoint.toString()}")
//                    initBottomSheet(markerlist.get(0))
//                }
//                return false
//
//            }
//
//            override fun onPressUpEvent(
//                p0: ArrayList<TMapMarkerItem>?,
//                p1: ArrayList<TMapPOIItem>?,
//                p2: TMapPoint?,
//                p3: PointF?
//            ): Boolean {
//                return false
//            }
//
//        })
    }

    private fun startLocationUpdates() {
        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
               // Permission 거절됨
            }
        }
    }
    private fun checkPermissionForLocation(context: Context) : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}

