package com.kravelteam.kravel_android.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.*
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.PhotoReviewRecyclerview
import com.kravelteam.kravel_android.ui.adapter.SearchDetailPlaceRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_search_detail.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.URL

class SearchDetailActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    private lateinit var photoAdapter: PhotoReviewRecyclerview
    private val networkManager : NetworkManager by inject()
    private val authManager: AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    private var id : Int = 0
    private var part : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)

        id = intent.getIntExtra("id",0)
        part = intent.getStringExtra("part")
        photoAdapter = PhotoReviewRecyclerview("default",part,id)
        txt_search_detail_title2.text = txt_search_detail_title2.text.toString().setCustomFontSubString(resources.getString(R.string.homeNewPhotoReview2),R.font.notosans_cjk_kr_bold,18)
        lottie = lottie_detail_loading

        swipe()

        initRecycler()
        initPlaceMoreBtn()
        initSetting()
    }

    private fun swipe(){
        swipe.setOnRefreshListener {

            initServer(true)
            swipe.isRefreshing = false
        }
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
        root.fadeInWithVisible(500)
        lottie_detail_loading.setGone()
    }

    private fun initSetting(){
        img_search_detail_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initPlaceMoreBtn(){
        //7개 이상일 때 더보기 버튼 활성화
        btn_search_detail_more.setOnDebounceClickListener {
            Intent(KravelApplication.GlobalApp,PlaceMoreActivity::class.java).apply {
                putExtra("id",id)
                putExtra("part",part)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { KravelApplication.GlobalApp.startActivity(this) }
        }
    }

    private fun initServer(refresh: Boolean){
        if(part == "celeb"){ //셀럽 디테일 상세 정보
            if(!refresh){
                onLoading()
            }
            if (newToken(authManager,networkManager)) {
                networkManager.requestCelebDetail(id,0,7).safeEnqueue(
                    onSuccess = {it ->
                        it.data.result.let {
                            GlideApp.with(this).load(it.celebrity.imageUrl).into(img_search_detail_title)
                            txt_search_detail_title.text = it.celebrity.celebrityName
                            txt_search_detail_sub2.text = resources.getString(R.string.visitedPlace)

                            when {
                                it.places.size == DATA_COUNT -> {
                                    placeAdapter.initData(it.places.dropLast(1).toMutableList())
                                    btn_search_detail_more.setVisible()
                                    img_search_place_empty.setGone()
                                    txt_search_place_empty.setGone()
                                }
                                it.places.isNullOrEmpty() -> {
                                    btn_search_detail_more.setGone()
                                    img_search_place_empty.setVisible()
                                    txt_search_place_empty.setVisible()
                                }
                                else -> {
                                    placeAdapter.initData(it.places.toMutableList())
                                    btn_search_detail_more.setGone()
                                    img_search_place_empty.setGone()
                                    txt_search_place_empty.setGone()
                                }
                            }
                        }
                        offLoading()
                    },
                    onFailure = {
                        if(it.code() == 403) {
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


                networkManager.getCelebPhotoReview(id,0,7,"reviewLikes-count,desc").safeEnqueue(
                    onSuccess = {
                        if(!it.data.result.content.isNullOrEmpty()) {
                            photoAdapter.initData(it.data.result.content)
                            txt_search_photo_empty.setGone()
                        } else {
                            txt_search_photo_empty.setVisible()
                        }
                    },
                    onFailure = {
                        if(it.code() == 403) {
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
                offLoading()
            }
        } else { //미디어 디테일 상세 정보
            if(!refresh){
                onLoading()
            }
            if (newToken(authManager,networkManager)) {
                networkManager.requestMediaDetail(id,0,7).safeEnqueue(
                    onSuccess = { it ->
                        it.data.result.let {
                            GlideApp.with(this).load(it.media.imageUrl).into(img_search_detail_title)
                            txt_search_detail_title.text = it.media.title
                            txt_search_detail_sub2.text = resources.getString(R.string.filmSite)

                            when {
                                it.places.size == DATA_COUNT -> {
                                    placeAdapter.initData(it.places.dropLast(1).toMutableList())
                                    btn_search_detail_more.setVisible()
                                    img_search_place_empty.setGone()
                                    txt_search_place_empty.setGone()
                                }
                                it.places.isNullOrEmpty() -> {
                                    btn_search_detail_more.setGone()
                                    img_search_place_empty.setVisible()
                                    txt_search_place_empty.setVisible()
                                }
                                else -> {
                                    placeAdapter.initData(it.places.toMutableList())
                                    btn_search_detail_more.setGone()
                                    img_search_place_empty.setGone()
                                    txt_search_place_empty.setGone()
                                }
                            }
                            offLoading()
                        }
                    },
                    onFailure = {
                        if(it.code() == 403) {
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


                networkManager.requestMediaPhotoReview(id,0,7,"reviewLikes-count,desc").safeEnqueue(
                    onSuccess = {
                        if(!it.data.result.content.isNullOrEmpty()) {
                            photoAdapter.initData(it.data.result.content)
                            txt_search_photo_empty.setGone()
                        } else {
                            txt_search_photo_empty.setVisible()
                        }
                    },
                    onFailure = {
                        if(it.code() == 403) {
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
                offLoading()
            }
        }
    }

    private fun initRecycler(){
        rv_search_detail_place.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }

        rv_search_detail_photo_review.apply{
            adapter = photoAdapter
            addItemDecoration(HorizontalItemDecorator(4))
            addItemDecoration(VerticalItemDecorator(4))
        }

        initServer(false)
    }

    override fun onResume() {
        super.onResume()
        initServer(false)
    }
    companion object {
        private const val DATA_COUNT = 7
    }
}