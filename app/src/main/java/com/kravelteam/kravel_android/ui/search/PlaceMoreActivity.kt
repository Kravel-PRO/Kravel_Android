package com.kravelteam.kravel_android.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.SearchDetailPlaceRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_place_more.*
import kotlinx.android.synthetic.main.activity_place_more.lottie_detail_loading
import org.koin.android.ext.android.inject

class PlaceMoreActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    private val networkManager : NetworkManager by inject()
    private val authManager: AuthManager by inject()
    private var page: Int = 0
    private var size: Int = 20
    private var id : Int = 0

    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_more)

        lottie = lottie_detail_loading

        id = intent.getIntExtra("id",0)
        initRecycler()
        img_place_more_back.setOnDebounceClickListener {
            finish()
        }
        btn_place_more.setOnDebounceClickListener {
            page++
            initPlaceMore(false)
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

    private fun initRecycler(){
        rv_place_more.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        initPlaceMore(true)
    }

    private fun initPlaceMore(init : Boolean){
        onLoading()
        if (newToken(authManager,networkManager)) {
            if(intent.getStringExtra("part") == "celeb"){ //셀럽이면
                networkManager.requestCelebDetail(id,page,size).safeEnqueue(
                    onSuccess = {
                        if(it.data.result.places.size == size) btn_place_more.setVisible()
                        else btn_place_more.setGone()

                        if(init) placeAdapter.initData(it.data.result.places.toMutableList())
                        else placeAdapter.addData(it.data.result.places.toMutableList())
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
            } else { //미디어이면
                networkManager.requestMediaDetail(id, page, size).safeEnqueue(
                    onSuccess = {
                        if (it.data.result.places.size == size) btn_place_more.setVisible()
                        else btn_place_more.setGone()

                        if (init) placeAdapter.initData(it.data.result.places.toMutableList())
                        else placeAdapter.addData(it.data.result.places.toMutableList())
                        offLoading()
                    },
                    onFailure = {
                        if (it.code() == 403) {
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
            }
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }
}