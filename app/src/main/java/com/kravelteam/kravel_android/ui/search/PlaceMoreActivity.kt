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
import com.kravelteam.kravel_android.util.safeEnqueue
import kotlinx.android.synthetic.main.activity_place_more.*
import org.koin.android.ext.android.inject

class PlaceMoreActivity : AppCompatActivity() {

    private val placeAdapter: SearchDetailPlaceRecyclerview by lazy { SearchDetailPlaceRecyclerview() }
    private val networkManager : NetworkManager by inject()

    private var id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_more)

        id = intent.getIntExtra("id",0)
        initRecycler()
        img_place_more_back.setOnDebounceClickListener {
            finish()
        }
    }

    private fun initRecycler(){
        rv_place_more.apply {
            adapter = placeAdapter
            addItemDecoration(HorizontalItemDecorator(8))
            addItemDecoration(VerticalItemDecorator(16))
        }

        networkManager.requestCelebDetail(id).safeEnqueue(
            onSuccess = {
                placeAdapter.initData(it.data.result.places)
            },
            onFailure = {},
            onError = {}
        )
    }
}