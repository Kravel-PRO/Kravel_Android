package com.kravelteam.kravel_android.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.NearPlaceDetailRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import kotlinx.android.synthetic.main.activity_near_place.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class NearPlaceActivity : AppCompatActivity() {

    private val placeAdapter : NearPlaceDetailRecyclerview by lazy { NearPlaceDetailRecyclerview() }
    private var latitude : Double? = null
    private var longitude : Double? = null
    private val networkManager : NetworkManager by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_place)

        latitude = intent.getDoubleExtra("latitude",0.0)
        longitude = intent.getDoubleExtra("longitude",0.0)

        initRecycler()
        img_near_place_back.setOnDebounceClickListener {
            finish()
        }
    }
    private fun initRecycler() {

        networkManager.getPlaceList(1.0,1.0).safeEnqueue(
            onSuccess = {
                rv_near_place.apply {
                    adapter = placeAdapter
                    addItemDecoration(VerticalItemDecorator(16))
                }

                placeAdapter.initData(it.data!!.result!!.content)
            },
            onFailure = {
                Timber.e("onFailure")
            },
            onError = {
                networkErrorToast()
            }
        )

    }
}

