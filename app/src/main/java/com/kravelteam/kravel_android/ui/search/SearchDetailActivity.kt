package com.kravelteam.kravel_android.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.data.response.PhotoResponse
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

    private var id : Int = 0
    private var part : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)

        id = intent.getIntExtra("id",0)
        part = intent.getStringExtra("part")
        photoAdapter = PhotoReviewRecyclerview("default",part,id)
        txt_search_detail_title2.text = txt_search_detail_title2.text.toString().setCustomFontSubString(resources.getString(R.string.homeNewPhotoReview2),R.font.notosans_cjk_kr_bold,18)

        initRecycler()
        initPlaceMoreBtn()
        initSetting()
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

    private fun initRecycler(){
        rv_search_detail_place.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }

        if(part == "celeb"){ //셀럽 디테일 상세 정보
            networkManager.requestCelebDetail(id).safeEnqueue(
                onSuccess = {it ->
                    it.data.result.let {
                        GlideApp.with(this).load(it.celebrity.imageUrl).into(img_search_detail_title)
                        txt_search_detail_title.text = it.celebrity.celebrityName
                        txt_search_detail_sub2.text = resources.getString(R.string.visitedPlace)

                        if( it.places.size == DATA_COUNT ){
                            placeAdapter.initData(it.places.dropLast(1))
                            btn_search_detail_more.setVisible()
                        } else {
                            placeAdapter.initData(it.places)
                            btn_search_detail_more.setGone()
                        }
                    }
                },
                onFailure = {},
                onError = {
                    networkErrorToast()
                }
            )


            networkManager.getCelebPhotoReview(id).safeEnqueue(
                onSuccess = {
                    rv_search_detail_photo_review.apply{
                        adapter = photoAdapter
                        addItemDecoration(HorizontalItemDecorator(4))
                        addItemDecoration(VerticalItemDecorator(4))
                    }
                    if(!it.data.result.content.isNullOrEmpty()) {
                        photoAdapter.initData(it.data.result.content)
                    }
                },
                onFailure = {
                    toast("실패")
                },
                onError = {
                    networkErrorToast()
                }
            )
        } else { //미디어 디테일 상세 정보
            networkManager.requestMediaDetail(id).safeEnqueue(
                onSuccess = { it ->
                    it.data.result.let {
                        GlideApp.with(this).load(it.imageUrl).into(img_search_detail_title)
                        txt_search_detail_title.text = it.title
                        txt_search_detail_sub2.text = resources.getString(R.string.filmSite)

                        if( it.places.size == DATA_COUNT ){
                            placeAdapter.initData(it.places.dropLast(1))
                            btn_search_detail_more.setVisible()
                        } else {
                            placeAdapter.initData(it.places)
                            btn_search_detail_more.setGone()
                        }
                    }
                },
                onFailure = {},
                onError = {
                    networkErrorToast()
                }
            )


            networkManager.requestMediaPhotoReview(id).safeEnqueue(
                onSuccess = {
                    rv_search_detail_photo_review.apply{
                        adapter = photoAdapter
                        addItemDecoration(HorizontalItemDecorator(4))
                        addItemDecoration(VerticalItemDecorator(4))
                    }
                    val data = it.data.result.content
                    data.isNullOrEmpty().let {
                        photoAdapter.initData(data)
                    }
                },
                onFailure = {
                    toast("실패")
                },
                onError = {
                    networkErrorToast()
                }
            )
        }
    }
    companion object {
        private const val DATA_COUNT = 7
    }
}