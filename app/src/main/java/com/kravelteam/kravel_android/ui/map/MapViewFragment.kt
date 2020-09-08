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
import kotlinx.android.synthetic.main.bottom_sheet_place.*
import kotlinx.android.synthetic.main.bottom_sheet_place.view.*
import kotlinx.android.synthetic.main.dialog_gps_permission.view.*
import kotlinx.android.synthetic.main.dialog_service_warning.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_info.*
import kotlinx.android.synthetic.main.fragment_map_info.view.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.URL


class MapViewFragment : Fragment(),OnMapReadyCallback, fragmentBackPressed{
    private lateinit var locationSource : FusedLocationSource
    private lateinit var mapFragment: MapFragment
    private var trackingmode : Boolean = false
    private var preMarker : Marker? = null
    private lateinit var naverMap : NaverMap
    private lateinit var mapFragment_Bottom : MapFragment
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetDetailBehavior : BottomSheetBehavior<ConstraintLayout>
    private lateinit var animMapInfo: LottieAnimationView
    private lateinit var root: View
    private  var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0
    private val nearplaceAdapter : MapNearPlaceRecyclerview by lazy { MapNearPlaceRecyclerview() } //BottomSheet_Detail
    private lateinit var photoAdapter : PhotoReviewRecyclerview //BottomSheet
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() } //BottomSheet
    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
    private var checkScrap : Boolean = false // BottomSheet
    private var checkScrapD : Boolean = false //BottomSheet_Detail
    private var mapMarker : Marker? = null
    private var checkReset : Boolean = false
    private var nearLocation : LatLng? = null
    private var checkBottomSheet : Boolean = false
    private var checkBottomSheetDetailClick : Boolean = false
    private var checkBottomSheetClick : Boolean = false
    private var checkFirst : Boolean = true
    private val networkManager : NetworkManager by inject()
    private var currentPlaceId : Int = 0
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
        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        animMapInfo = animMapInfoLottie
        root = requireView().findViewById(R.id.root)
        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(cl_bottom_seat_place)
        bottomSheetDetailBehavior = BottomSheetBehavior.from<ConstraintLayout>(cl_bottom_sheet_map_detail)

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



        img_bottom_photo.setOnClickListener {
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
        cl_bottom_sheet_map_detail.rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        cl_bottom_sheet_map_detail.rv_map_detail_photo.apply {
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

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
        val fm = childFragmentManager
        mapFragment_Bottom = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }

        if(mapMarker?.map !=null) {
            mapMarker?.map = null
        }
        checkBottomSheet = false
        Timber.e("BottomSheetClick!!")
        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                txt_bottom_title.text = it.data.result.title
                txt_bottom_map_address1.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(img_bottom_place).load(it.data.result.imageUrl).into(img_bottom_place)
                }
                Timber.e("BottomSheetServer Connection!!")
                // Round값 물어보기
                img_bottom_place.setRound(10.dpToPx().toFloat())

                mapFragment_Bottom.getMapAsync{naver1Map ->
                    mapMarker = Marker()
                    mapMarker!!.position = marker.position
                    naver1Map!!.moveCamera(CameraUpdate.scrollTo(marker!!.position))
                    mapMarker!!.map = naver1Map
                    mapMarker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                }
                hashtagAdapter.initData(it.data.result.tags)
                initPhotoReview("BOTTOM",it.data.result.placeId)
                checkScrap = it.data.result.scrap
                if(checkScrap) {
                    GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill).into(cl_bottom_seat_place.img_user_scrap)
                }
                cl_bottom_seat_place.img_bottom_photo_edit.setOnDebounceClickListener {
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
                        mapFragment.getMapAsync{naverMap ->
                            rv_map_near_place.setVisible()
                            markerInfo.markerClick = false
                            preMarker = null
                            mapFragment_Bottom.onDestroy()
                            marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                            cl_bottom_seat_place.setGone()
                            togglebtn_gps.setVisible()
                            img_reset.setVisible()
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        initAnimation()
                        (activity as AppCompatActivity).cl_main_bottom?.setGone()
                        checkBottomSheet = true
                        Handler().postDelayed({
                            initBottomSheetDetail(placeId)
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

    private fun initPhotoReview(mode : String,placeId: Int) {
        photoAdapter = PhotoReviewRecyclerview("default","place",placeId)
        networkManager.getPlaceReview(placeId).safeEnqueue(
            onSuccess = {
                when(mode) {
                    "BOTTOM" -> {
                        if(!it.data.result.content.isNullOrEmpty()) {

                            photoAdapter.initData(it.data.result.content)
                        }
                        return@safeEnqueue
                    }
                    "BOTTOM_D" -> {
                        if(!it.data.result.content.isNullOrEmpty()) {
                            photoAdapter.initData(it.data.result.content)
                        }
                        return@safeEnqueue

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

        cl_bottom_sheet_map_detail.rv_map_detail_photo.apply {
            adapter = photoAdapter
        }

        cl_bottom_seat_place.rv_home_photo_review.apply {
            adapter = photoAdapter
        }
    }
    private fun initBottomSheetDetail(placeId : Int) {
        currentPlaceId = placeId
        val fm = childFragmentManager
        mapFragment_Bottom = fm.findFragmentById(R.id.place_detail_map) as MapFragment?
            ?:MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.place_detail_map,it).commit()
            }

        if(mapMarker?.map !=null) {
            mapMarker?.map = null
        }
        cl_bottom_seat_place.setGone()
        cl_bottom_sheet_map_detail.setVisible()
        bottomSheetDetailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        checkBottomSheetClick = true
        checkBottomSheetDetailClick =false
        onResume()
        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                cl_bottom_sheet_map_detail.txt_map_detail_title.text = it.data.result.title
                cl_bottom_sheet_map_detail.txt_map_detail_address.text = it.data.result.location
                cl_bottom_sheet_map_detail.txt_map_detail_address2.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(cl_bottom_sheet_map_detail.img_map_detail_place).load(it.data.result.imageUrl).into(cl_bottom_sheet_map_detail.img_map_detail_place)
                }

                mapFragment_Bottom.getMapAsync{naver2Map ->
                    mapMarker = Marker()
                    mapMarker!!.position = LatLng(it.data.result.latitude,it.data.result.longitude)
                    naver2Map.moveCamera(CameraUpdate.scrollTo(mapMarker!!.position))
                    mapMarker!!.map = naver2Map
                    mapMarker!!.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

                }

                hashtagAdapter.initData(it.data.result.tags)

                cl_bottom_sheet_map_detail.txt_map_detail_bus_content.text = it.data.result.bus
                cl_bottom_sheet_map_detail.txt_map_detail_subway_content.text = it.data.result.subway
                initPhotoReview("BOTTOM_D",placeId)
                initNearPlaceRecycler(it.data.result.latitude,it.data.result.longitude)

                checkScrapD = it.data.result.scrap
                if(checkScrapD) {
                    GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill).into(cl_bottom_sheet_map_detail.img_map_detail_scrap)
                }

                cl_bottom_sheet_map_detail.img_photo_review_edit.setOnDebounceClickListener {
                    checkBottomSheetDetailClick = true
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

        bottomSheetDetailBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        checkBottomSheet = false
                        checkBottomSheetDetailClick =false
                        cl_bottom_seat_place.setVisible()
                        (activity as AppCompatActivity).cl_main_bottom?.setVisible()
                        cl_bottom_sheet_map_detail.setGone()
                        mapFragment_Bottom.onDestroy()
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        checkBottomSheet = false
                        checkBottomSheetDetailClick = false
                        cl_bottom_seat_place.setVisible()
                        (activity as AppCompatActivity).cl_main_bottom?.setVisible()
                        cl_bottom_sheet_map_detail.setGone()
                        mapFragment_Bottom.onDestroy()

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        cl_bottom_sheet_map_detail.img_map_detail_scrap.setOnDebounceClickListener {
            if(checkScrapD) {
                Timber.e("checkScrap true -> false")
                //checkScrap == TRUE
                networkManager.postScrap(placeId, ScrapBody(false) ).safeEnqueue (
                    onSuccess = {
                        checkScrapD = false
                        GlideApp.with(GlobalApp).load(R.drawable.ic_scrap).into(cl_bottom_sheet_map_detail.img_map_detail_scrap)
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
                        checkScrapD = true
                        GlideApp.with(KravelApplication.GlobalApp).load(R.drawable.ic_scrap_fill).into(cl_bottom_sheet_map_detail.img_map_detail_scrap)

                    }, onFailure = {
                        Timber.e("실패")

                    },
                    onError = {
                        networkErrorToast()
                    })
            }
        }
    }
    private fun initNearPlaceRecycler(latitude: Double, longitude: Double) {
        cl_bottom_sheet_map_detail.rv_map_detail_near_place.apply {
            adapter = nearplaceAdapter
            addItemDecoration(HorizontalItemDecorator(12))
        }

        val local = "eng"
        var url : URL? = null
        if(local == "kor") {
            url= URL(
                "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?&MobileOS=AND&MobileApp=Kravel&radius=1000"
                        + "&ServiceKey=" + resources.getString(R.string.open_api_kor_place)
                        + "&mapX=${longitude}&mapY=${latitude}"
            )

        } else {
            url = URL(
                "http://api.visitkorea.or.kr/openapi/service/rest/EngService/locationBasedList?&MobileOS=AND&MobileApp=Kravel&radius=1000"
                        + "&ServiceKey=" + resources.getString(R.string.open_api_eng_place)
                        + "&mapX=${longitude}&mapY=${latitude}")
        }

        val handler: Handler = object : Handler() {
            override fun handleMessage(msg: Message?) {

                val parserHandler = XmlPullParserHandler()
                val neardatas = parserHandler.parse(url.openStream())

                nearplaceAdapter.initData(neardatas)
                nearplaceAdapter.notifyDataSetChanged()
            }
        }


        object : Thread() {
            override fun run() {
                val msg = handler.obtainMessage()
                handler.sendMessage(msg)
            }
        }.start()
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
                    // Timber.e("marker.tag ::: ${marker.tag}")
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
        }
        if(checkBottomSheetDetailClick) {
            initBottomSheetDetail(currentPlaceId)
        }

    }

    override fun onBackPressed(): Boolean {
        return if (checkBottomSheet) {
            onResume()
            cl_bottom_seat_place.setVisible()
            cl_bottom_sheet_map_detail.setGone()
            (activity as AppCompatActivity).cl_main_bottom?.setVisible()
            bottomSheetBehavior.isFitToContents  = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            checkBottomSheet = false
            checkBottomSheetDetailClick = false
            true
        } else {
            false
        }
    }


}




