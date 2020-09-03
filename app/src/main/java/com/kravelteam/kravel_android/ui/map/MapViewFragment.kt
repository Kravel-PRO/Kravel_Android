package com.kravelteam.kravel_android.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.MapNearPlaceData
import com.kravelteam.kravel_android.data.mock.NearPlaceData
import com.kravelteam.kravel_android.data.mock.PlaceInformationData
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.*
import com.kravelteam.kravel_android.util.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.LocationTrackingMode.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_place.*
import kotlinx.android.synthetic.main.bottom_sheet_place.view.*
import kotlinx.android.synthetic.main.dialog_gps_permission.view.*
import kotlinx.android.synthetic.main.dialog_service_warning.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.rv_home_photo_review
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_info.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.koin.android.ext.android.inject
import timber.log.Timber


class MapViewFragment : Fragment(),OnMapReadyCallback{
    private lateinit var locationSource : FusedLocationSource
    private lateinit var mapFragment: MapFragment
    private var trackingmode : Boolean = false
    private var markerClick : Boolean = false
    private lateinit var naverMap : NaverMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetDetailBehavior : BottomSheetBehavior<ConstraintLayout>
    private lateinit var animMapInfo: LottieAnimationView
    private lateinit var root: View

    private val nearplaceAdapter : MapNearPlaceRecyclerview by lazy { MapNearPlaceRecyclerview() } //BottomSheet_Detail
    private val photoAdapter : PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() } //BottomSheet
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() } //BottomSheet


    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }

    private val networkManager : NetworkManager by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if(!trackingmode) {
                trackingmode = true
                naverMap.locationTrackingMode = Follow

            } else {
                trackingmode = false
                naverMap.locationTrackingMode = NoFollow
            }
        }


        img_bottom_photo.setOnClickListener {
             Intent(GlobalApp,CameraActivity::class.java).run {
                 GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }

        //initAreaWarningDailog()
        initRecycler()

    }

    private fun initAreaWarningDailog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity()).create()
        val view = LayoutInflater.from(GlobalApp).inflate(R.layout.dialog_service_warning, null)
        view.cl_area_warning_background.setBackgroundColor(Color.TRANSPARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.btn_area_warning_ok setOnDebounceClickListener {
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }
    }
    private fun initBottomSheet(marker: Marker, placeId : Int) {

        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                txt_bottom_title.text = it.data.result.title
                txt_bottom_map_address1.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(img_bottom_place).load(it.data.result.imageUrl).into(img_bottom_place)
                }
                // Round값 물어보기
                img_bottom_place.setRound(10.dpToPx().toFloat())

                initMap("BOTTOM",it.data.result.latitude,it.data.result.longitude)
                rv_map_hashtag.apply {
                    adapter = hashtagAdapter
                    addItemDecoration(HorizontalItemDecorator(4))
                }
                hashtagAdapter.initData(it.data.result.tags)
                initPhotoReview("BOTTOM",it.data.result.placeId)

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
                            markerClick = false
                            marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
                            cl_bottom_seat_place.setGone()
                            togglebtn_gps.setVisible()
                            img_reset.setVisible()
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        initAnimation()
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        (activity as AppCompatActivity).cl_main_bottom?.setGone()
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

    }

    private fun initPhotoReview(mode : String,placeId: Int) {
        networkManager.getPlaceReview(placeId).safeEnqueue(
            onSuccess = {
                when(mode) {
                    "BOTTOM" -> {
                        rv_home_photo_review.apply {
                            adapter = photoAdapter
                            addItemDecoration(VerticalItemDecorator(4))
                            addItemDecoration(HorizontalItemDecorator(4))
                        }
                        if(!it.data.result.content.isNullOrEmpty()) {
                            photoAdapter.initData(it.data.result.content)
                        }
                    }
                    "BOTTOM_D" -> {
                        cl_bottom_sheet_map_detail.rv_map_detail_photo.apply {
                            adapter = photoAdapter
                            addItemDecoration(VerticalItemDecorator(4))
                            addItemDecoration(HorizontalItemDecorator(4))
                        }
                        if(!it.data.result.content.isNullOrEmpty()) {
                            photoAdapter.initData(it.data.result.content)
                        }

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



    }
    private fun initBottomSheetDetail(placeId : Int) {

        cl_bottom_seat_place.setGone()
        cl_bottom_sheet_map_detail.setVisible()
        bottomSheetDetailBehavior.setPeekHeight(Resources.getSystem().displayMetrics.heightPixels)
        bottomSheetDetailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDetailBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onResume()

                when(newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        cl_bottom_seat_place.setVisible()
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        (activity as AppCompatActivity).cl_main_bottom?.setVisible()
                        cl_bottom_sheet_map_detail.setGone()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                cl_bottom_sheet_map_detail.txt_map_detail_title.text = it.data.result.title
                cl_bottom_sheet_map_detail.txt_map_detail_address.text = it.data.result.location
                cl_bottom_sheet_map_detail.txt_map_detail_address2.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(cl_bottom_sheet_map_detail.img_map_detail_place).load(it.data.result.imageUrl).into(cl_bottom_sheet_map_detail.img_map_detail_place)
                }

                initMap("BOTTOM_D",it.data.result.latitude,it.data.result.longitude)
                cl_bottom_sheet_map_detail.rv_map_detail_hashtag.apply {
                    adapter = hashtagAdapter
                    addItemDecoration(HorizontalItemDecorator(4))
                }
                hashtagAdapter.initData(it.data.result.tags)

                cl_bottom_sheet_map_detail.txt_map_detail_bus_content.text = it.data.result.bus
                cl_bottom_sheet_map_detail.txt_map_detail_subway_content.text = it.data.result.subway
                initPhotoReview("BOTTOM_D",placeId)

            },
            onFailure = {
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }
        )
    }
    private fun initAnimation() {

        animMapInfo.apply {
            setAnimation("loading_map.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        animMapInfoLottie.setVisible()
    }

    /**
     * BottomSheet 안의 Map 띄우기
     */
    private fun initMap(mode:String,latitude : Double, longitude : Double) {
        val fm = childFragmentManager
        when(mode) {
            "BOTTOM" -> {
                val map2Fragment = fm.findFragmentById(R.id.mapView) as MapFragment?
                    ?: MapFragment.newInstance().also {
                        fm.beginTransaction().add(R.id.mapView, it).commit()
                    }

                map2Fragment.getMapAsync{naverMap ->
                    val marker = Marker()
                    marker.position = LatLng(latitude,longitude)
                    naverMap.moveCamera(CameraUpdate.scrollTo(marker.position))
                    marker.map = naverMap
                    marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

                }

            }
            "BOTTOM_D" -> {
                val mapBottomFragment = fm.findFragmentById(R.id.place_detail_map) as MapFragment?
                    ?:MapFragment.newInstance().also {
                        fm.beginTransaction().add(R.id.place_detail_map,it).commit()
                    }

                mapBottomFragment.getMapAsync{naverMap ->
                    val marker = Marker()
                    marker.position = LatLng(latitude,longitude)
                    naverMap.moveCamera(CameraUpdate.scrollTo(marker.position))
                    marker.map = naverMap
                    marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

                }
            }
        }




    }
    private fun initRecycler() {

        /**
         * latitude,longitude 바꿔줘야함!
         */
        nearAdapter.setOnItemClickListener(object : MapPlaceRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PlaceContentResponse, pos: Int) {
                Intent(GlobalApp,PlaceDetailActivity::class.java).apply {
                    putExtra("placeId",data.placeId)
                }.run {
                    GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }

        })
        networkManager.getPlaceList(1.0,1.0).safeEnqueue (
            onSuccess = {
                rv_map_near_place.apply {
                    adapter = nearAdapter
                    addItemDecoration(HorizontalItemDecorator(16))
                }

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
        naverMap.locationTrackingMode = Follow
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false



        naverMap.addOnCameraChangeListener{reason,animated ->
            if(reason == CameraUpdate.REASON_GESTURE) {
                togglebtn_gps.isChecked = false
                trackingmode = false
            }
        }

        val placeId = 1
        val marker = com.naver.maps.map.overlay.Marker()
        marker.position = LatLng(37.5606311, 126.9936153)
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
        marker.setOnClickListener { overlay ->
            if(!markerClick) {
                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                rv_map_near_place.setGone()
                initBottomSheet(marker,placeId)
                markerClick = true
            } else {
                markerClick = false
                rv_map_near_place.setVisible()
                cl_bottom_seat_place.setGone()
                togglebtn_gps.setVisible()
                img_reset.setVisible()
                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
            }

            true
        }

        naverMap.setOnMapClickListener { pointF, latLng ->
            if(markerClick) {
                markerClick = false
                rv_map_near_place.setVisible()
                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
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

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        root.setVisible()
        animMapInfo.setGone()

    }

}


