package com.kravelteam.kravel_android.ui.map

import android.app.Activity
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.ScrapBody
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.HashTagRecyclerView
import com.kravelteam.kravel_android.ui.adapter.MapNearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.startActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_place_detail.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.URL


class PlaceDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private val hashtagAdapter : HashTagRecyclerView by lazy { HashTagRecyclerView() }
    private lateinit var photoAdapter : PhotoReviewRecyclerview
    private val nearplaceAdapter : MapNearPlaceRecyclerview by lazy { MapNearPlaceRecyclerview() } //BottomSheet
    private var placeId : Int = 0
    private lateinit var naverMap: NaverMap
    private val networkManager : NetworkManager by inject()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private var checkScrap : Boolean = false
    private var part: String = "place"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        placeId = intent.getIntExtra("placeId", 0)
        setResult(Activity.RESULT_OK)

        photoAdapter = PhotoReviewRecyclerview("default",part,placeId)

        img_photo_review_edit.setOnDebounceClickListener {
            Intent(GlobalApp,PostReviewActivity::class.java).apply{
                putExtra("placeId",placeId)
                putExtra("part",part)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run {
                GlobalApp.startActivity(this)
            }
        }

        img_map_detail_arrow.setOnClickListener {
            finish()
        }

        initSetting()
        img_map_detail_photo.setOnClickListener {
               Intent(GlobalApp, CameraActivity::class.java).run {
                   GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }

        img_map_detail_scrap.setOnClickListener {
            Timber.e("placeID //////${placeId}")
            Timber.e("scrap ////////${checkScrap}")
            if(checkScrap) {
                Timber.e("checkScrap true -> false")
                //checkScrap == TRUE
                networkManager.postScrap(placeId, ScrapBody(false) ).safeEnqueue (
                        onSuccess = {
                            checkScrap = false
                            GlideApp.with(img_map_detail_scrap).load(R.drawable.ic_scrap).into(img_map_detail_scrap)
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
                        GlideApp.with(img_map_detail_scrap).load(R.drawable.ic_scrap_fill).into(img_map_detail_scrap)

                    }, onFailure = {
                        Timber.e("실패")

                    },
                    onError = {
                        networkErrorToast()
                    })
            }
        }

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

                initNearPlaceRecycler(it.data.result.latitude, it.data.result.longitude)

                checkScrap = it.data.result.scrap
                if(checkScrap) {
                    GlideApp.with(applicationContext).load(R.drawable.ic_scrap_fill).into(img_map_detail_scrap)
                }

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
    }
    private fun initNearPlaceRecycler(latitude: Double, longitude: Double) {
        rv_map_detail_near_place.apply {
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