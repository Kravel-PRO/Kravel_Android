package com.kravelteam.kravel_android.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.*
import com.kravelteam.kravel_android.data.request.ScrapBody
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.*
import com.kravelteam.kravel_android.util.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.LocationTrackingMode.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_gps_permission.view.*
import kotlinx.android.synthetic.main.dialog_service_warning.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_info.*
import kotlinx.android.synthetic.main.fragment_map_info.view.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.naver.maps.map.MapFragment
import kotlinx.android.synthetic.main.activity_photo_review.*
import java.lang.Math.round
import kotlin.math.roundToInt


class MapViewFragment : Fragment(),OnMapReadyCallback, fragmentBackPressed{
    private lateinit var locationSource : FusedLocationSource
    private lateinit var mapFragment: MapFragment
    private var trackingmode : Boolean = false
    private var preMarker : Marker? = null
    private lateinit var naverMap : NaverMap
    private  var mapFragment_Bottom : MapFragment? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var animMapInfo: LottieAnimationView
    private lateinit var root: View
    private  var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0
    private val authManager : AuthManager by inject()
    private lateinit var photoAdapter : PhotoReviewRecyclerview //BottomSheet
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() } //BottomSheet
    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
    private var checkScrap : Boolean = false // BottomSheet
    private var mapMarker : Marker? = null
    private var checkReset : Boolean = false
    private var nearLocation : LatLng? = null
    private var checkBottom : Boolean =false
    private var checkBottomSheetClick : Boolean = false
    private var checkFirst : Boolean = true
    private val networkManager : NetworkManager by inject()
    private var scale : Double = 5.0
    private lateinit var childFragment : FragmentManager
    private var part: String = "place"
    private var filterImg: String? = null
    private var placeName : String = ""
    private var subImg: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        childFragment = childFragmentManager
        mapFragment = childFragment.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragment.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
        animMapInfo = animMapInfoLottie
        root = requireView().findViewById(R.id.root)
        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(cl_bottom_seat_place)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlterMessageNoGPS()
        }



        togglebtn_gps.setOnDebounceClickListener {
            if(checkArea(mLatitude,mLongitude)) {
                if (!trackingmode) {
                    trackingmode = true
                    naverMap.locationTrackingMode = Follow

                } else {
                    trackingmode = false
                    naverMap.locationTrackingMode = NoFollow
                }
            } else {
                togglebtn_gps.isSelected = false
                initAreaWarningDailog()
            }
        }

        img_reset.setOnClickListener {
            if(!checkReset) {
                Timber.e("CameraMove?")
                initRecycler(nearLocation!!.latitude,nearLocation!!.longitude,scale)
                checkReset = true
            }
        }



        img_bottom_photo.setOnDebounceClickListener {
             Intent(GlobalApp,CameraActivity::class.java).apply {
                 putExtra("filter",filterImg)
                 putExtra("placeName",placeName)
                 putExtra("subImg",subImg)
                 addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             }.run { GlobalApp.startActivity(this) }
        }

        init()

    }
    private fun checkArea(latitude: Double, longitude: Double) : Boolean {
        var checkAreaBool = false
        var checkLat = false
        var checkLng = false
        if(latitude in 33.0..43.0) {
            checkLat = true
        }
        if(longitude in 124.0..132.0) {
            checkLng = true
        }

        if(checkLat && checkLng) {
            checkAreaBool = true
        }

        return checkAreaBool
    }
    private fun init() {
        cl_bottom_seat_place.rv_home_photo_review.apply {
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

        rv_map_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        rv_map_near_place.apply {
            adapter = nearAdapter
            addItemDecoration(HorizontalItemDecorator(16))
        }

    }

    private fun initAreaWarningDailog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity()).create()
        val view = LayoutInflater.from(GlobalApp).inflate(R.layout.dialog_service_warning, null)
        view.cl_area_warning_background.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.btn_area_warning_ok setOnDebounceClickListener {
            dialog.cancel()
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }
    }
    private fun initBottomSheet(marker: Marker) {
        val markerInfo : TagMarkerData = marker!!.tag as TagMarkerData
        val placeId =  markerInfo.placeId
        mapFragment_Bottom = childFragment.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragment.beginTransaction().add(R.id.mapView, it).commit()
            }

        if(mapMarker?.map !=null) {
            mapMarker?.map = null
        }
        checkBottom = true
        Timber.e("BottomSheetClick!!")
        if(newToken(authManager,networkManager)) {
            networkManager.getPlaceDetailList(placeId).safeEnqueue(
                onSuccess = {
                    txt_bottom_title.text = it.data.result.title
                    txt_bottom_map_address1.text = it.data.result.location
                    it.data.result.filterImageUrl?.let {
                        filterImg = it
                    }
                    it.data.result.subImageUrl?.let {
                        subImg = it
                    }
                    placeName = it.data.result.title
                    if (!it.data.result.imageUrl.isNullOrBlank()) {
                        GlideApp.with(img_bottom_place).load(it.data.result.imageUrl)
                            .into(img_bottom_place)
                    }
                    Timber.e("BottomSheetServer Connection!!")
                    img_bottom_place.setRound(10.dpToPx().toFloat())

                    mapFragment_Bottom!!.getMapAsync { navermap ->
                        if (mapMarker?.map != null) {
                            mapMarker?.map = null
                        }
                        val uiSettings = navermap.uiSettings
                        uiSettings.isZoomControlEnabled = false
                        uiSettings.isTiltGesturesEnabled = false
                        uiSettings.isZoomGesturesEnabled = false
                        uiSettings.isScrollGesturesEnabled = false
                        val Marker =
                            Marker(LatLng(it.data.result.latitude, it.data.result.longitude))
                        navermap.moveCamera(CameraUpdate.scrollTo(Marker!!.position))
                        Marker!!.map = navermap
                        Marker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                        mapMarker = Marker
                    }

                    if (!it.data.result.tags.isNullOrEmpty()) {
                        val str = it.data.result.tags!!.split(",")
                        rv_map_hashtag.setVisible()
                        hashtagAdapter.initData(str)
                    } else {
                        rv_map_hashtag.setGone()
                    }


                    initPhotoReview(it.data.result.placeId)
                    checkScrap = it.data.result.scrap
                    if (checkScrap) {
                        GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill)
                            .into(cl_bottom_seat_place.img_user_scrap)
                    }
                    cl_bottom_seat_place.img_bottom_photo_edit.setOnDebounceClickListener {
                        checkBottomSheetClick = true
                        Intent(GlobalApp, PostReviewActivity::class.java).apply {
                            putExtra("placeId", placeId)
                            putExtra("part", part)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.run {
                            GlobalApp.startActivity(this)
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
        cl_bottom_seat_place.setVisible()
        togglebtn_gps.setGone()
        img_reset.setGone()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBack()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        initAnimation()
                        checkBottomSheetClick = true
                        childFragmentManager.beginTransaction().remove(mapFragment_Bottom!!).commit()
                        Handler().postDelayed({
                            Intent(GlobalApp,PlaceDetailActivity::class.java).apply {
                                putExtra("placeId",placeId)
                                putExtra("mode","map")
                            }.run {
                                GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }

                        }, 1500)

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        cl_bottom_seat_place.img_user_scrap.setOnDebounceClickListener {
            if(checkScrap) {
                Timber.e("checkScrap true -> false")
                //checkScrap == TRUE
                if(newToken(authManager,networkManager)) {
                    networkManager.postScrap(placeId, ScrapBody(false)).safeEnqueue(
                        onSuccess = {
                            checkScrap = false
                            GlideApp.with(GlobalApp).load(R.drawable.ic_scrap)
                                .into(cl_bottom_seat_place.img_user_scrap)
                        }, onFailure = {
                            if (it.code() == 403) {
                                toast(resources.getString(R.string.errorReLogin))
                            } else {
                                toast(resources.getString(R.string.errorClient))
                            }

                        },
                        onError = {
                            networkErrorToast()
                        })
                } else {
                    toast(resources.getString(R.string.errorNetwork))
                }

            } else {
                Timber.e("checkScrap false -> true")
                if(newToken(authManager,networkManager)) {
                    networkManager.postScrap(placeId, ScrapBody(true)).safeEnqueue(
                        onSuccess = {
                            checkScrap = true
                            GlideApp.with(KravelApplication.GlobalApp)
                                .load(R.drawable.ic_scrap_fill)
                                .into(cl_bottom_seat_place.img_user_scrap)

                        }, onFailure = {
                            if (it.code() == 403) {
                                toast(resources.getString(R.string.errorReLogin))
                            } else {
                                toast(resources.getString(R.string.errorClient))
                            }

                        },
                        onError = {
                            networkErrorToast()
                        })
                }else{
                    toast(resources.getString(R.string.errorNetwork))
                }
            }
        }

    }

    private fun initPhotoReview(placeId: Int) {
        photoAdapter = PhotoReviewRecyclerview("default","place",placeId)
        if(newToken(authManager,networkManager)) {
            networkManager.getPlaceReview(placeId, 0, 7, "reviewLikes-count,desc").safeEnqueue(
                onSuccess = {
                    if (!it.data.result.content.isNullOrEmpty()) {
                        txt_bottom_photo_empty.setGone()
                        photoAdapter.initData(it.data.result.content)
                        cl_bottom_seat_place.rv_home_photo_review.setVisible()
                        return@safeEnqueue
                    } else {
                        cl_bottom_seat_place.rv_home_photo_review.setGone()
                        txt_bottom_photo_empty.setVisible()

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

        cl_bottom_seat_place.rv_home_photo_review.apply {
            adapter = photoAdapter
        }
    }
    private fun initAnimation() {

        img_bottom_bar.setGone()
        animMapInfo.apply {
            setAnimation("loading_map.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        animMapInfoLottie.setVisible()
    }

    private fun initRecycler(latitude: Double, longitude: Double, scale : Double) {
        nearAdapter.setOnItemClickListener(object : MapPlaceRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PlaceContentResponse, pos: Int) {
                Intent(GlobalApp,PlaceDetailActivity::class.java).apply {
                    putExtra("placeId",data.placeId)
                    putExtra("mode","home")
                }.run {
                    GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }

        })
        if(newToken(authManager,networkManager)) {
            networkManager.getPlaceList(latitude, longitude, scale, scale).safeEnqueue(
                onSuccess = {
                    nearAdapter.initData(it.data!!.result!!.content)
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

    private fun initMarker() {
        if(newToken(authManager,networkManager)) {
            networkManager.getMapMarkerList().safeEnqueue(
                onSuccess = {
                    for (i in 0..it.data.result.size - 1) {
                        val marker = com.naver.maps.map.overlay.Marker()
                        marker.position =
                            LatLng(it.data.result.get(i).latitude, it.data.result.get(i).longitude)
                        marker.map = naverMap
                        marker.tag = TagMarkerData(it.data.result.get(i).placeId, false)
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                        val listener = Overlay.OnClickListener { overlay ->

                            val markerData = marker.tag!! as TagMarkerData

                            if (preMarker == null) {
                                if (!markerData.markerClick) {
                                    markerData.markerClick = true
                                    preMarker = marker
                                    marker.icon =
                                        OverlayImage.fromResource(R.drawable.ic_mark_focus)
                                    rv_map_near_place.setGone()
                                    initBottomSheet(marker)
                                }
                            } else {
                                if (preMarker == marker) {
                                    markerData.markerClick = false
                                    preMarker = null
                                    marker.icon =
                                        OverlayImage.fromResource(R.drawable.ic_mark_default)
                                    rv_map_near_place.setVisible()
                                    cl_bottom_seat_place.setGone()
                                } else {
                                    val preMarkerData = preMarker!!.tag!! as TagMarkerData
                                    preMarkerData.markerClick = false
                                    preMarker!!.icon =
                                        OverlayImage.fromResource(R.drawable.ic_mark_default)
                                    markerData.markerClick = true
                                    marker.icon =
                                        OverlayImage.fromResource(R.drawable.ic_mark_focus)
                                    preMarker = marker
                                    rv_map_near_place.setGone()
                                    initBottomSheet(marker)
                                }
                            }
                            true
                        }
                        marker.onClickListener = listener

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
    /*
    핸드폰 gps 가 꺼져있을 시 , gps를 키기위한 함수
     */
    private fun buildAlterMessageNoGPS() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity()).create()
        val view = LayoutInflater.from(GlobalApp).inflate(R.layout.dialog_gps_permission, null)
        view.cl_gps_transparent.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.btn_gps_yes.setOnDebounceClickListener {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),11)
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {

           mLatitude = locationSource.lastLocation!!.latitude!!
           mLongitude = locationSource.lastLocation!!.longitude!!
           if(!locationSource.isActivated) {
               naverMap.locationTrackingMode = None
           }
           return
       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = NoFollow

        initMarker()

        naverMap.addOnLocationChangeListener {
            if(checkFirst) {
                if(checkArea(it.latitude,it.longitude)) {
                    naverMap.locationTrackingMode = Follow
                }
                else {
                    naverMap.locationTrackingMode = None
                    val projection = naverMap.projection
                    val masterPerDp = projection.metersPerDp
                    scale = (((masterPerDp/12)/88/2)).toString().substring(0,5).toDouble()
                    nearLocation = LatLng(naverMap.cameraPosition.target.latitude ,naverMap.cameraPosition.target.longitude)
                    initRecycler(nearLocation!!.latitude,nearLocation!!.longitude,scale)
                }
                checkFirst = false
            }
        }

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

        naverMap.addOnCameraChangeListener{reason,animated ->
            if(reason == CameraUpdate.REASON_GESTURE) {
                togglebtn_gps.isSelected = false
                trackingmode = false
            }
            if(reason == CameraUpdate.REASON_LOCATION){
                mLatitude = locationSource.lastLocation!!.latitude
                mLongitude = locationSource.lastLocation!!.longitude
                if(checkArea(mLatitude,mLongitude)) {
                    togglebtn_gps.isSelected = false
                    initRecycler(mLatitude,mLongitude,0.025)
                    trackingmode = false
                } else {
                    naverMap.locationTrackingMode = None
                }

            }

            if(reason == CameraUpdate.REASON_GESTURE || reason == CameraUpdate.REASON_LOCATION) {
                nearLocation = LatLng(naverMap.cameraPosition.target.latitude ,naverMap.cameraPosition.target.longitude)
                val projection = naverMap.projection
                val masterPerDp = projection.metersPerDp
                scale = (((masterPerDp/12)/88/2)).toString().substring(0,5).toDouble()

                Timber.e("scale :: ${scale}")
                if(preMarker != null) {
                    preMarker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                    val preMarkerData = preMarker!!.tag as TagMarkerData
                    preMarkerData.markerClick = false
                    preMarker = null
                    rv_map_near_place.setVisible()
                    cl_bottom_seat_place.setGone()
                    togglebtn_gps.setVisible()
                    img_reset.setVisible()
                }
                checkReset = false
            }
        }


        naverMap.setOnMapClickListener { pointF, latLng ->
            if(preMarker != null) {
                preMarker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                val preMarkerData = preMarker!!.tag as TagMarkerData
                preMarkerData.markerClick = false
                preMarker = null
                rv_map_near_place.setVisible()
                cl_bottom_seat_place.setGone()
                togglebtn_gps.setVisible()
                img_reset.setVisible()
            }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        root.setVisible()
        animMapInfo.setGone()
        img_bottom_bar.setVisible()
        if(checkBottomSheetClick) {
            initBottomSheet(preMarker!!)
            Timber.e("bottomsheet reset")
            checkBottomSheetClick = false
        }
    }

    private fun bottomSheetBack() {
        if(preMarker != null) {
            Timber.e("BottomSheet Back")
            preMarker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
            val preMarkerData = preMarker!!.tag as TagMarkerData
            preMarkerData.markerClick = false
            preMarker = null
            childFragmentManager.beginTransaction().remove(mapFragment_Bottom!!).commit()
            rv_map_near_place.setVisible()
            cl_bottom_seat_place.setGone()
            togglebtn_gps.setVisible()
            img_reset.setVisible()
        }
        checkBottom = false
    }
    override fun onBackPressed() : Boolean {
        return if (checkBottom) {
            bottomSheetBack()
            false
        } else {
            true
        }
    }


}




