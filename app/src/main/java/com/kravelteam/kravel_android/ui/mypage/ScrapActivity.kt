package com.kravelteam.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.MyScrapData
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.ScrapRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.setVisible
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_scrap.*
import org.koin.android.ext.android.inject

class ScrapActivity : AppCompatActivity() {

    private val scrapAdapter: ScrapRecyclerview by lazy { ScrapRecyclerview() }
    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrap)

        initRecycler()
        initSetting()
    }

    private fun initSetting(){
        img_scrap_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        rv_scrap.apply {
            adapter = scrapAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        if (newToken(authManager,networkManager)) {
            networkManager.requestMyScrap().safeEnqueue(
                onSuccess = {
                    if(it.data.result.content.isNullOrEmpty()){
                        img_scrap_empty_icon.setVisible()
                        txt_scrap_content.setVisible()
                    } else{
                        scrapAdapter.initData(it.data.result.content)
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
        }
    }
}