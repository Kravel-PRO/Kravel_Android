package com.kravelteam.kravel_android.ui.mypage

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
import com.kravelteam.kravel_android.ui.adapter.ScrapRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_all_photo_review.*
import kotlinx.android.synthetic.main.activity_scrap.*
import kotlinx.android.synthetic.main.activity_scrap.lottie_detail_loading
import kotlinx.android.synthetic.main.activity_scrap.root
import org.koin.android.ext.android.inject
import timber.log.Timber

class ScrapActivity : AppCompatActivity() {

    private lateinit var scrapAdapter: ScrapRecyclerview
    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrap)

        lottie = lottie_detail_loading

        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        img_scrap_back.setOnDebounceClickListener {
            finish()
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
        scrapAdapter = ScrapRecyclerview()
        rv_scrap.apply {
            adapter = scrapAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        onLoading()
        if (newToken(authManager,networkManager)) {
            networkManager.requestMyScrap().safeEnqueue(
                onSuccess = {
                    if(it.data.result.content.isNullOrEmpty()){
                        img_scrap_empty_icon.setVisible()
                        txt_scrap_content.setVisible()
                    } else{
                        rv_scrap.setVisible()
                        scrapAdapter.initData(it.data.result.content)
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
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }
}