package com.kravelteam.kravel_android.ui.map

import android.app.Activity
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.*
import com.kravelteam.kravel_android.data.request.ScrapBody
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.*
import com.kravelteam.kravel_android.util.*
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
    private val imageAdapter: MapDetailRecyclerview by lazy { MapDetailRecyclerview() }
    private var placeId : Int = 0
    private lateinit var naverMap: NaverMap
    private val networkManager : NetworkManager by inject()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private var checkScrap : Boolean = false
    private var part: String = "place"
    private var placeName : String = ""
    private var subImg: String? = null
    private var filterImg: String? = null
    private val authManager : AuthManager by inject()
    private val  image = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)



        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        placeId = intent.getIntExtra("placeId", 0)
        val mode = intent.getStringExtra("mode")

        if(mode.equals("map")) {
            this.overridePendingTransition(R.anim.transition_in, R.anim.transition_out)
        }
        setResult(Activity.RESULT_OK)

        photoAdapter = PhotoReviewRecyclerview("default",part,placeId)
        rv_map_detail_photo.apply {
            adapter = photoAdapter
            addItemDecoration(VerticalItemDecorator(4))
            addItemDecoration(HorizontalItemDecorator(4))
        }

        img_photo_review_edit.setOnDebounceClickListener {
            Intent(GlobalApp,PostReviewActivity::class.java).apply{
                putExtra("placeId",placeId)
                putExtra("part",part)
                putExtra("subImg",subImg)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run {
                GlobalApp.startActivity(this)
            }
        }

        img_map_detail_arrow.setOnClickListener {
           finish()
        }

        vp_map_detail_place.apply {
            adapter = imageAdapter
            ar_indicator.numberOfIndicators = 2
            if(vp_map_detail_place.onFlingListener == null) {
                ar_indicator.attachTo(this, true)
            }
        }


        img_map_detail_photo.setOnClickListener {
            Intent(GlobalApp,CameraActivity::class.java).apply {
                putExtra("filter",filterImg)
                putExtra("placeName",placeName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { GlobalApp.startActivity(this) }
        }

        img_map_detail_scrap.setOnClickListener {
            Timber.e("placeID //////${placeId}")
            Timber.e("scrap ////////${checkScrap}")
            if(checkScrap) {
                Timber.e("checkScrap true -> false")
                //checkScrap == TRUE
                if (newToken(authManager, networkManager)){
                    networkManager.postScrap(placeId, ScrapBody(false)).safeEnqueue(
                        onSuccess = {
                            checkScrap = false
                            GlideApp.with(img_map_detail_scrap).load(R.drawable.ic_scrap)
                                .into(img_map_detail_scrap)
                        }, onFailure = {
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

            } else {
                Timber.e("checkScrap false -> true")
                if(newToken(authManager,networkManager)) {
                    networkManager.postScrap(placeId, ScrapBody(true)).safeEnqueue(
                        onSuccess = {
                            checkScrap = true
                            GlideApp.with(img_map_detail_scrap).load(R.drawable.ic_scrap_fill)
                                .into(img_map_detail_scrap)

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
            }
        }

    }
    private fun initSetting() {
        if(newToken(authManager,networkManager)) {
            networkManager.getPlaceDetailList(placeId).safeEnqueue(
                onSuccess = {
                    txt_map_detail_title.text = it.data.result.title
                    txt_map_detail_address.text = it.data.result.location
                    txt_map_detail_address2.text = it.data.result.location
                    it.data.result.filterImageUrl?.let {
                        filterImg = it
                    }
                    it.data.result.subImageUrl?.let {
                        subImg = it
                    }
                    placeName = it.data.result.title
                    if (!it.data.result.imageUrl.isNullOrBlank()) {
                        image.add(it.data.result.imageUrl)
                    }
                    if (!it.data.result.subImageUrl.isNullOrBlank()) {
                        image.add(it.data.result.subImageUrl)

                    } else {
                        image.add(R.color.colorDarkGrey.toString())
                    }

                    if (!image.isNullOrEmpty()) {
                        imageAdapter.initData(image)

                    }



                    latitude = it.data.result.latitude
                    longitude = it.data.result.longitude
                    txt_map_detail_bus_content.text = it.data.result.bus
                    txt_map_detail_subway_content.text = it.data.result.subway

                    initNearPlaceRecycler(it.data.result.latitude, it.data.result.longitude)

                    checkScrap = it.data.result.scrap
                    Timber.e("checkScrap :::::: " + checkScrap)
                    if (checkScrap) {
                        GlideApp.with(applicationContext).load(R.drawable.ic_scrap_fill)
                            .into(img_map_detail_scrap)
                    }

                    initMap()
                    initHashTag(it.data.result.tags)


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

        initPhotoRecycler()
    }
    private fun initNearPlaceRecycler(latitude: Double, longitude: Double) {
        rv_map_detail_near_place.apply {
            adapter = nearplaceAdapter
            addItemDecoration(HorizontalItemDecorator(12))
        }

        val local = authManager.setLang
        Timber.e("local? ::$local")
        var url : URL? = null
        if(local == "KOR") {
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

                if(neardatas.isNullOrEmpty()) {
                    cl_bottom_near_place.setGone()
                } else{
                    nearplaceAdapter.initData(neardatas)
                    cl_bottom_near_place.setVisible()
                }

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

    private fun initHashTag(data: String?) {
        rv_map_detail_hashtag.apply {
            adapter = hashtagAdapter
            addItemDecoration(HorizontalItemDecorator(4))
        }

        if(!data.isNullOrEmpty()) {
            val str = data!!.split(",")
            hashtagAdapter.initData(str)
        }
    }
    private fun initPhotoRecycler() {
        if(newToken(authManager,networkManager)) {
            networkManager.getPlaceReview(placeId, 0, 7, "reviewLikes-count,desc").safeEnqueue(
                onSuccess = {

                    if (!it.data.result.content.isNullOrEmpty()) {
                        photoAdapter.initData(it.data.result.content)
                        txt_map_detail_photo_review_empty.setGone()
                        rv_map_detail_photo.setVisible()
                    } else {
                        rv_map_detail_photo.setGone()
                        txt_map_detail_photo_review_empty.setVisible()
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

    override fun onResume() {
        initSetting()
        super.onResume()
    }

}