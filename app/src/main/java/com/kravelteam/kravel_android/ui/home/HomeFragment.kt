package com.kravelteam.kravel_android.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.ui.adapter.NearRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PopularRecyclerview
import com.kravelteam.kravel_android.ui.map.MapViewFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
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

        mLocationRequest = LocationRequest()
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlterMessageNoGPS()
        }

        if(checkPermissionForLocation(requireContext())) {
            startLocationUpdates()
        }


        init()
        initPopularRecycler()
        initPhotoRecycler()
        initNearRecycler()
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
    private fun init() {
        txt_home_near_place_more.setOnDebounceClickListener {
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
    companion object {
        private const val REQUEST_PERMISSION_LOCATION = 1000
        private const val INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 1000
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

    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
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
        if(mLastLocation != location) {
            Timber.e("mLocationLat ${mLastLocation.latitude}")
            Timber.e("mLocationLng ${mLastLocation.longitude}")
        }



    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    override fun onStop() {
        super.onStop()
        stoplocationUpdates()
    }




}
