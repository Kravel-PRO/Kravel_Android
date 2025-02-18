package com.kravelteam.kravel_android.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isNotEmpty
import androidx.core.view.marginBottom
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.*
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.PopularPlaceData
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.NearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PopularRecyclerview
import com.kravelteam.kravel_android.ui.map.PlaceDetailActivity
import com.kravelteam.kravel_android.ui.map.fragmentBackPressed
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.dialog_gps_permission.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.lottie_detail_loading
import org.koin.android.ext.android.inject
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), fragmentBackPressed {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private var latitude : Double? = null
    private var longitude : Double?=null
    private val popularAdapter : PopularRecyclerview by lazy { PopularRecyclerview() }
    private lateinit var photoAdapter : PhotoReviewRecyclerview
    private val nearAdapter : NearPlaceRecyclerview by lazy { NearPlaceRecyclerview() }
    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lottie = lottie_detail_loading
        cl_home_near_place.setGone()
        photoAdapter = PhotoReviewRecyclerview("new","", -1)
        rv_home_photo.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }
        rv_popular_place.apply {
            adapter = popularAdapter
            addItemDecoration(VerticalItemDecorator(12))
        }
        rv_near_place.apply {
            adapter = nearAdapter
            addItemDecoration(HorizontalItemDecorator(16))
        }

        mLocationRequest = LocationRequest()
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && checkPermissionForLocation(requireContext())) {
            buildAlterMessageNoGPS()
        }

        if(checkPermissionForLocation(requireContext())) {
            startLocationUpdates()
        }
        initPopularRecycler()
        initPhotoRecycler()

        sw_refresh_layout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                initPopularRecycler()
                initPhotoRecycler()
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && checkPermissionForLocation(requireContext())) {
                    startLocationUpdates()
                }
                sw_refresh_layout.isRefreshing = false

            }

        })


    }

    private fun onLoading(){
        lottie.apply {
            setAnimation("heart_loading.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        lottie_detail_loading.setVisible()
    }

    private fun offLoading(){
        lottie_detail_loading.setGone()
        root.fadeInWithVisible(500)
    }
    /*
  핸드폰 gps 가 꺼져있을 시 , gps를 키기위한 함수
   */
    private fun buildAlterMessageNoGPS() {
        val dialog = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_gps_permission, null)
        view.cl_gps_transparent.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.btn_gps_yes.setOnDebounceClickListener {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),11)
            dialog.cancel()

        }
        view.btn_gps_no.setOnDebounceClickListener {
            dialog.cancel()
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }

    }
    private fun initNearRecycler(latitude : Double?, longitude : Double?) {

            stoplocationUpdates()
            nearAdapter.setOnItemClickListener(object : NearPlaceRecyclerview.OnItemClickListener {
                override fun onItemClick(v: View, data: PlaceContentResponse, pos: Int) {
                    Intent(GlobalApp, PlaceDetailActivity::class.java).apply {
                        putExtra("placeId", data.placeId)
                        putExtra("mode", "home")
                    }.run {
                        GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }

            })
            if (newToken(authManager, networkManager)) {
                networkManager.getPlaceList(latitude!!, longitude!!, 0.05, 0.05).safeEnqueue(
                    onSuccess = {
                        if (!it.data!!.result.content.isNullOrEmpty()) {
                            nearAdapter.initData(it.data!!.result.content)
                            if(cl_home_near_place!=null) {
                                cl_home_near_place.setVisible()

                                cl_home_near.setOnDebounceClickListener {
                                    Intent(GlobalApp, NearPlaceActivity::class.java).apply {
                                        putExtra("latitude", latitude!!)
                                        putExtra("longitude", longitude!!)
                                    }.run {
                                        GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                    }
                                }
                            }
                        } else {
                            if (cl_home_near_place != null) {
                                cl_home_near_place.setGone()
                            }
                        }


                    },
                    onFailure = {
                        if (it.code() == 403) {
                            toast(resources.getString(R.string.errorReLogin))
                        } else {
                            toast(resources.getString(R.string.errorClient))
                        }

                    },
                    onError = {
                        networkErrorToast()
                    }
                )
            } else {
                toast(resources.getString(R.string.errorNetwork))
            }

    }
    private fun initPopularRecycler() {


        popularAdapter.setOnItemClickListener(object : PopularRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PlaceContentResponse, pos: Int) {
                   Intent(GlobalApp,PlaceDetailActivity::class.java).apply {
                       putExtra("placeId",data.placeId)
                       putExtra("mode","home")
                   }.run {
                       GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                   }
            }

        })
        onLoading()
        if(newToken(authManager,networkManager)) {
            networkManager.getPopularPlaceList("review-count,desc").safeEnqueue (
                onSuccess = {
                    if(it.data.result.content.isNullOrEmpty()) {
                        cl_home_popular_empty.setVisible()
                        rv_popular_place.setGone()
                    } else {
                        popularAdapter.initData(it.data!!.result.content)
                        cl_home_popular_empty.setGone()
                        rv_popular_place.setVisible()
                    }
                    offLoading()
                },
                onFailure = {
                    if (it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }

    }
    companion object {
        private const val REQUEST_PERMISSION_LOCATION = 1000
        private const val INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 1000
    }
    private fun initPhotoRecycler() {
        if(newToken(authManager,networkManager)) {
            networkManager.getPhotoReview(0, 7, "createdDate,desc").safeEnqueue(
                onSuccess = {


                    if (it.data.result.content.isNullOrEmpty()) {
                        txt_home_photo_review_empty!!.setVisible()
                        rv_home_photo.setGone()
                    } else {
                        photoAdapter.initData(it.data.result.content)
                        txt_home_photo_review_empty!!.setGone()
                        rv_home_photo.setVisible()
                    }
                },
                onFailure = {
                    if (it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                },
                onError = {
                    networkErrorToast()
                }
            )
        }else{
            toast(resources.getString(R.string.errorNetwork))
        }

    }

    protected fun startLocationUpdates() {
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            mLastLocation= locationResult.lastLocation
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            onLocationChanged(locationResult.lastLocation)


        }
    }

    fun onLocationChanged(location: Location) {
        mLastLocation = location
        Timber.e("latitude : ${latitude}")
        Timber.e("longitude : ${longitude}")
        if(latitude!=null && longitude!=null) {
            initNearRecycler(latitude, longitude)
        }
    }

    private fun stoplocationUpdates() {
        if(mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        }
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
                Timber.e("gps 권한 True - checkPermission ")
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                Timber.e("gps 권한 false - checkPermission ")
                false
            }
        } else {

            true
        }
    }

    override fun onBackPressed() : Boolean {
        return true
    }




}
