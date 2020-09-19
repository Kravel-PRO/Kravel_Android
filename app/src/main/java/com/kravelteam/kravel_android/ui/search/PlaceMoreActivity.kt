package com.kravelteam.kravel_android.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.response.DetailPlaceResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.SearchDetailPlaceRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_place_more.*
import org.koin.android.ext.android.inject

class PlaceMoreActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    private val networkManager : NetworkManager by inject()
    private var page: Int = 0
    private var size: Int = 20

    private var id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_more)

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

    private fun initRecycler(){
        rv_place_more.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }
        initPlaceMore(true)
    }

    private fun initPlaceMore(init : Boolean){
        networkManager.requestCelebDetail(id,page,size).safeEnqueue(
            onSuccess = {
                if(it.data.result.places.size == size) btn_place_more.setVisible()
                else btn_place_more.setGone()

                if(init) placeAdapter.initData(it.data.result.places.toMutableList())
                else placeAdapter.addData(it.data.result.places.toMutableList())
            },
            onFailure = {
                if(it.code() == 403) {
                    toast("재로그인을 해주세요!")
                } else {
                    toast("리스트 불러오기에 실패했습니다")
                }
            },
            onError = {
                networkErrorToast()
            }
        )
    }
}