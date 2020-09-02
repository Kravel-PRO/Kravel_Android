package com.kravelteam.kravel_android.ui.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.davidmiguel.dragtoclose.DragListener
import com.davidmiguel.dragtoclose.DragToClose
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.HashTagData
import com.kravelteam.kravel_android.data.mock.PlaceInformationData
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.MapNearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_info.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.properties.Delegates

class PlaceDetailActivity : AppCompatActivity() , OnMapReadyCallback {
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    private val photoAdapter : PhotoReviewRecyclerview by lazy {PhotoReviewRecyclerview()}
    private val nearplaceAdapter : MapNearPlaceRecyclerview by lazy { MapNearPlaceRecyclerview() } //BottomSheet
    private var placeId : Int = 0
    private lateinit var naverMap: NaverMap
    private val networkManager : NetworkManager by inject()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        placeId = intent.getIntExtra("placeId",0)
        setResult(Activity.RESULT_OK)
        img_map_detail_arrow.setOnClickListener {
            finish()
        }
        img_map_detail_photo.setOnClickListener {
               Intent(GlobalApp,CameraActivity::class.java).run {
                   GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
        initSetting()

        sv_place_detail.setOnScrollChangeListener(object : View.OnScrollChangeListener{
            override fun onScrollChange(
                v: View?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                Timber.e("scrollY ::::::::::" + scrollY)
                Timber.e("oldscrollY:::::::::::::::::"+oldScrollY)
                if(scrollY == 0 ) {
                    Timber.e("scroll!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    val dragToClose = findViewById<DragToClose>(R.id.drag_to_close)
                    dragToClose.setDragListener(object : DragListener{
                        override fun onDragging(dragOffset: Float) {
                        }

                        override fun onStartDraggingView() {
                        }

                        override fun onViewCosed() {
                        }

                    })
                    sv_place_detail.setOnClickListener {
                        dragToClose.closeDraggableContainer()
                    }
                }


            }

        })

    }
    private fun initSetting() {


        networkManager.getPlaceDetailList(placeId).safeEnqueue (
            onSuccess = {
                txt_map_detail_title.text = it.data.result.title
                txt_map_detail_address.text = it.data.result.location
                txt_map_detail_address2.text = it.data.result.location
                if(!it.data.result.imageUrl.isNullOrBlank()) {
                    GlideApp.with(applicationContext).load(it.data.result.imageUrl)
                        .into(img_map_detail_place)
                }
                latitude = it.data.result.latitude
                longitude = it.data.result.longitude
                txt_map_detail_bus_content.text = it.data.result.bus
                txt_map_detail_subway_content.text = it.data.result.subway

                initMap()
                initHashTag(it.data.result.tags)

            },
            onFailure = {
                Timber.e("실패")

            },
            onError = {
                networkErrorToast()
            }
        )


        initPhotoRecycler()
        initNearPlaceRecycler()
    }
    private fun initNearPlaceRecycler() {
        rv_map_detail_near_place.apply {
            adapter =nearplaceAdapter
            addItemDecoration(HorizontalItemDecorator(12))
        }

       // nearplaceAdapter.initData(placeInfo.placeNearPlace)
    }
    private fun initMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.place_detail_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.place_detail_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun initHashTag(data : Array<String>?) {
        rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        hashtagAdapter.initData(data)
    }
    private fun initPhotoRecycler() {
        rv_map_detail_photo.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

      //  photoAdapter.initData(placeInfo.placePhotoReview)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
        naverMap.moveCamera(CameraUpdate.scrollTo(marker.position))
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.ic_mark_focus)

    }
}