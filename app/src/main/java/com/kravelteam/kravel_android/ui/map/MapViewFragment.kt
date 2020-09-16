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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
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
    private lateinit var childFragment : FragmentManager
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
                togglebtn_gps.isChecked = false
                initAreaWarningDailog()
            }
        }

        img_reset.setOnClickListener {
            if(!checkReset) {
                Timber.e("CameraMove?")
                initRecycler(nearLocation!!.latitude,nearLocation!!.longitude)
                checkReset = true
            }
        }



        img_bottom_photo.setOnDebounceClickListener {
             Intent(GlobalApp,CameraActivity::class.java).run {
                 GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
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
        Timber.e("BottomSheetClick!!")
        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                txt_bottom_title.text = it.data.result.title
                txt_bottom_map_address1.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(img_bottom_place).load(it.data.result.imageUrl).into(img_bottom_place)
                }
                Timber.e("BottomSheetServer Connection!!")
                img_bottom_place.setRound(10.dpToPx().toFloat())

                mapFragment_Bottom!!.getMapAsync{navermap ->
                    if(mapMarker?.map !=null) {
                        mapMarker?.map = null
                    }
                    val uiSettings = navermap.uiSettings
                    uiSettings.isZoomControlEnabled = false
                    uiSettings.isTiltGesturesEnabled = false
                    uiSettings.isZoomGesturesEnabled = false
                    uiSettings.isScrollGesturesEnabled = false
                    val  Marker = Marker(LatLng(it.data.result.latitude,it.data.result.longitude))
                    navermap.moveCamera(CameraUpdate.scrollTo(Marker!!.position))
                    Marker!!.map = navermap
                    Marker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                    mapMarker = Marker
                }

                if(!it.data.result.tags.isNullOrEmpty()) {
                    val str = it.data.result.tags!!.split(",")
                    hashtagAdapter.initData(str)
                }


                initPhotoReview(it.data.result.placeId)
                checkScrap = it.data.result.scrap
                if(checkScrap) {
                    GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill).into(cl_bottom_seat_place.img_user_scrap)
                }
                cl_bottom_seat_place.img_bottom_photo_edit.setOnDebounceClickListener {
                    checkBottomSheetClick = true
                    Intent(GlobalApp,PostReviewActivity::class.java).apply{
                        putExtra("placeId",placeId)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.run {
                        GlobalApp.startActivity(this)
                    }
                }

            },
            onFailure = {
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }
        )
        togglebtn_gps.setGone()
        img_reset.setGone()
        cl_bottom_seat_place.setVisible()


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBack()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        initAnimation()
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
                networkManager.postScrap(placeId, ScrapBody(false) ).safeEnqueue (
                    onSuccess = {
                        checkScrap = false
                        GlideApp.with(GlobalApp).load(R.drawable.ic_scrap).into(cl_bottom_seat_place.img_user_scrap)
                    }, onFailure = {
                        Timber.e("실패")

                    },
                    onError = {
                        networkErrorToast()
                    })


            } else {
                Timber.e("checkScrap false -> true")
                networkManager.postScrap(placeId, ScrapBody(true)).safeEnqueue (
                    onSuccess = {
                        checkScrap = true
                        GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill).into(cl_bottom_seat_place.img_user_scrap)

                    }, onFailure = {
                        Timber.e("실패")

                    },
                    onError = {
                        networkErrorToast()
                    })
            }
        }

    }

    private fun initPhotoReview(placeId: Int) {
        photoAdapter = PhotoReviewRecyclerview("default","place",placeId)
        networkManager.getPlaceReview(placeId).safeEnqueue(
            onSuccess = {
                if(!it.data.result.content.isNullOrEmpty()) {
                    photoAdapter.initData(it.data.result.content)
                    return@safeEnqueue
                }
            },
            onFailure = {
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }
        )

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

    private fun initRecycler(latitude: Double, longitude: Double) {
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
        networkManager.getPlaceList(latitude, longitude).safeEnqueue (
            onSuccess = {
                nearAdapter.initData(it.data!!.result!!.content)
            },
            onFailure = {
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }
        )
    }

    private fun initMarker(latitude: Double, longitude: Double) {
        networkManager.getMapMarkerList(latitude,longitude).safeEnqueue (
            onSuccess = {
                for(i in 0..it.data.result.size-1) {
                    val marker = com.naver.maps.map.overlay.Marker()
                    marker.position = LatLng(it.data.result.get(i).latitude, it.data.result.get(i).longitude)
                    marker.map = naverMap
                    marker.tag = TagMarkerData(it.data.result.get(i).placeId, false)
                    marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                    val listener = Overlay.OnClickListener { overlay ->

                        val markerData = marker.tag!! as TagMarkerData

                        if(preMarker== null){
                            if(!markerData.markerClick) {
                                markerData.markerClick = true
                                preMarker = marker
                                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                                rv_map_near_place.setGone()
                                initBottomSheet(marker)
                            }
                        } else {
                            if (preMarker == marker) {
                                markerData.markerClick = false
                                preMarker = null
                                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                                rv_map_near_place.setVisible()
                                cl_bottom_seat_place.setGone()
                            } else {
                                val preMarkerData = preMarker!!.tag!! as TagMarkerData
                                preMarkerData.markerClick = false
                                preMarker!!.icon =
                                    OverlayImage.fromResource(R.drawable.ic_mark_default)
                                markerData.markerClick = true
                                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
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
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }

        )
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

        naverMap.addOnLocationChangeListener {
            if(checkFirst) {
                if(checkArea(it.latitude,it.longitude)) {
                    naverMap.locationTrackingMode = Follow
                }
                else {
                    naverMap.locationTrackingMode = None
                    nearLocation = LatLng(naverMap.cameraPosition.target.latitude ,naverMap.cameraPosition.target.longitude)
                    initMarker(nearLocation!!.latitude,nearLocation!!.longitude)
                    initRecycler(nearLocation!!.latitude,nearLocation!!.longitude)
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
                togglebtn_gps.isChecked = false
                trackingmode = false
            }
            if(reason == CameraUpdate.REASON_LOCATION){
                mLatitude = locationSource.lastLocation!!.latitude
                mLongitude = locationSource.lastLocation!!.longitude
                if(checkArea(mLatitude,mLongitude)) {
                    togglebtn_gps.isChecked = false
                    initMarker(mLatitude,mLongitude)
                    initRecycler(mLatitude,mLongitude)
                    trackingmode = false
                } else {
                    naverMap.locationTrackingMode = None
                }

            }

            if(reason == CameraUpdate.REASON_GESTURE || reason == CameraUpdate.REASON_LOCATION) {
                nearLocation = LatLng(naverMap.cameraPosition.target.latitude ,naverMap.cameraPosition.target.longitude)
                initMarker(nearLocation!!.latitude,nearLocation!!.longitude)
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
            true
        } else {
            false
        }
    }


}




