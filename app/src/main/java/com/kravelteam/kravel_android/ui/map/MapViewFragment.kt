package com.kravelteam.kravel_android.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
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
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.MapPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.camera.CameraFragment
import com.kravelteam.kravel_android.util.dpToPx
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setRound
import com.kravelteam.kravel_android.util.setVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.LocationTrackingMode.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.dialog_service_warning.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.rv_home_photo_review
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_info.*
import kotlinx.android.synthetic.main.fragment_user.*
import timber.log.Timber


class MapViewFragment : Fragment(),OnMapReadyCallback{
    private lateinit var locationSource : FusedLocationSource
    private lateinit var mapFragment: MapFragment
    private var trackingmode : Boolean = false
    private var markerClick : Boolean = false
    private lateinit var naverMap : NaverMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var animMapInfo: LottieAnimationView
    private lateinit var root: View

    private val photoAdapter : PhotoReviewRecyclerview by lazy { PhotoReviewRecyclerview() } //BottomSheet
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() } //BottomSheet

    private val nearAdapter: MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
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


        root = requireView().findViewById(R.id.root)
        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(cl_bottom_seat_place)

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

        view.txt_area_warning_content1.text = "서비스가 지원되는 지역이 아니예요"
        view.txt_area_warning_content2.text ="한국에서 크래블을 즐겨봐요!"
        view.btn_area_warning_ok.text ="확인"
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
    private fun initBottomSheet(marker: Marker) {
        val placeInfo : PlaceInformationData = PlaceInformationData(
            placeName = "호텔델루나",
            placeAddress = "호텔델루나 주소",
            placePhotoReview = arrayListOf( PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg"),
                PhotoResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg"),
                PhotoResponse("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg")
            ),
            placeTag =  arrayListOf( HashTagData("호텔델루나"),
                HashTagData("아이유"),
                HashTagData("여진구")),
            placeImg = "https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg",
            marker = marker.position,
            placeBus = "2020,1102,1110",
            placeSubway = "7호선 숭실대입구역",
            placeNearPlace = arrayListOf(
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","남산"),
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","서울"),
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","경복궁"),
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","지도"),
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","안녕"),
                MapNearPlaceData("https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","남산")
            )
        )



        GlideApp.with(img_bottom_place).load(placeInfo.placeImg).into(img_bottom_place)
        // Round값 물어보기
        img_bottom_place.setRound(10.dpToPx().toFloat())
        txt_bottom_title.text = placeInfo.placeName
        txt_bottom_map_address1.text =  placeInfo.placeAddress
       // txt_bottom_map_address2.text = placeInfo.placeAddress

        rv_map_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        hashtagAdapter.initData(placeInfo.placeTag)

        rv_home_photo_review.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

        photoAdapter.initData(placeInfo.placePhotoReview)

        initMap(placeInfo.marker)


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
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        initAnimation()

                        Handler().postDelayed({
                            Intent(GlobalApp, PlaceDetailActivity::class.java).apply {
                                putExtra("data", placeInfo)
                            }.run {
                                requireActivity().startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION))
//                                requireActivity().startActivityForResult(
//                                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION),
//                                    REQUEST_MAP_DETAIL_ACTIVITY
//                                )
                            }
                        }, 1500)

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })

    }
    private fun initAnimation() {

        animMapInfo = animMapInfoLottie
        animMapInfo.apply {
            setAnimation("loading_small.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        animMapInfoLottie.setVisible()
    }
    private fun initMap(markerItemLatLng: LatLng) {
        val fm = childFragmentManager
        val map2Fragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }

        map2Fragment.getMapAsync{naverMap ->
            val marker = Marker()
            naverMap.moveCamera(CameraUpdate.scrollTo(markerItemLatLng))
            marker.position = markerItemLatLng
            marker.map = naverMap
            marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

        }

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

        val marker = com.naver.maps.map.overlay.Marker()
        marker.position = LatLng(37.5606311, 126.9936153)
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_default)
        marker.setOnClickListener { overlay ->
            if(!markerClick) {
                marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)
                rv_map_near_place.setGone()
                initBottomSheet(marker)
                markerClick = true
            } else {
                markerClick = false
                rv_map_near_place.setVisible()
                cl_bottom_seat_place.setGone()
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
            }
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        requireActivity()
//        if( requestCode == REQUEST_MAP_DETAIL_ACTIVITY) {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            root.setVisible()
//        }
//    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onResume() {
        super.onResume()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        root.setVisible()
    }

}


