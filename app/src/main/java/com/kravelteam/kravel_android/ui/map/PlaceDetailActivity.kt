package com.kravelteam.kravel_android.ui.map

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.mock.MapNearPlaceData
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.MapNearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_place_detail.*
import org.koin.android.ext.android.inject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.net.URL


class PlaceDetailActivity : AppCompatActivity() , OnMapReadyCallback {
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    private val photoAdapter : PhotoReviewRecyclerview by lazy {PhotoReviewRecyclerview()}
    private val nearplaceAdapter : MapNearPlaceRecyclerview by lazy { MapNearPlaceRecyclerview() } //BottomSheet
    private var placeId : Int = 0
    private lateinit var naverMap: NaverMap
    private val networkManager : NetworkManager by inject()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0

    val datas = mutableListOf<MapNearPlaceData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        placeId = intent.getIntExtra("placeId", 0)
        setResult(Activity.RESULT_OK)
        img_map_detail_arrow.setOnClickListener {
            finish()
        }
        img_map_detail_photo.setOnClickListener {
               Intent(GlobalApp, CameraActivity::class.java).run {
                   GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
        initSetting()

    }
    private fun initSetting() {
        networkManager.getPlaceDetailList(placeId).safeEnqueue(
            onSuccess = {
                txt_map_detail_title.text = it.data.result.title
                txt_map_detail_address.text = it.data.result.location
                txt_map_detail_address2.text = it.data.result.location
                if (!it.data.result.imageUrl.isNullOrBlank()) {
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

        initNearPlaceRecycler(latitude, longitude)
        initPhotoRecycler()

    }
    private fun initNearPlaceRecycler(latitude: Double, longitude: Double) {
        getXmlData()
        rv_map_detail_near_place.apply {
            adapter = nearplaceAdapter
            addItemDecoration(HorizontalItemDecorator(12))
        }

        nearplaceAdapter.initData(datas)

    }
    private fun getXmlData() {
        var checkImage = false
        var checkTitle = false
        var title : String? = null
        var image : String? = null
        try {
            var url: URL = URL(
                "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?&MobileOS=AND&MobileApp=Kravel&radius=1000"
                        + "&ServiceKey=" + R.string.open_api_kor_place
                        + "&mapX=126.981611&mapY=37.568477"
            )
//                + "&mapX="+longitude.toString()+"&mapY="+latitude.toString())

            val parseCreator: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            val parser: XmlPullParser = parseCreator.newPullParser()

            parser.setInput(url.openStream(), null)

            var parserEvent = parser.eventType

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                when (parserEvent) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("title")) {
                            checkTitle = true
                        }
                        if (parser.name.equals("firstimage")) {
                            checkImage = true
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (checkTitle) {
                            Timber.e("Title:::: ${parser.text}")
                            title = parser.text
                            checkTitle = false
                        }
                        if (checkImage) {
                            Timber.e("Image:::: ${parser.text}")
                            image = parser.text
                            checkImage = false
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name.equals("item")) {
                            Timber.e("ddddd?")
                            datas.add(MapNearPlaceData(image, title));
                        }
                    }
                }
                parserEvent = parser.next()
            }
        } catch (e: Exception) {
            Timber.e("에러")
        }
    }
    private fun initMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.place_detail_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.place_detail_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun initHashTag(data: Array<String>?) {
        rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        hashtagAdapter.initData(data)
    }
    private fun initPhotoRecycler() {


        networkManager.getPlaceReview(placeId).safeEnqueue(
            onSuccess = {
                rv_map_detail_photo.apply {
                    adapter = photoAdapter
                    addItemDecoration(VerticalItemDecorator(4))
                    addItemDecoration(HorizontalItemDecorator(4))
                }

                if (!it.data.result.content.isNullOrEmpty()) {
                    photoAdapter.initData(it.data.result.content)
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