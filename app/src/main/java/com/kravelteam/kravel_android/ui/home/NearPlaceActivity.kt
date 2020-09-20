package com.kravelteam.kravel_android.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.mock.NewPhotoReview
import com.kravelteam.kravel_android.data.response.PlaceContentResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.NearPlaceDetailRecyclerview
import com.kravelteam.kravel_android.ui.adapter.NearPlaceRecyclerview
import com.kravelteam.kravel_android.ui.map.PlaceDetailActivity
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_near_place.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class NearPlaceActivity : AppCompatActivity() {

    private val placeAdapter : NearPlaceDetailRecyclerview by lazy { NearPlaceDetailRecyclerview() }
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
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


        placeAdapter.setOnItemClickListener(object : NearPlaceDetailRecyclerview.OnItemClickListener {
            override fun onItemClick(v: View, data: PlaceContentResponse, pos: Int) {
                Intent(KravelApplication.GlobalApp, PlaceDetailActivity::class.java).apply {
                    putExtra("placeId",data.placeId)
                    putExtra("mode","home")
                }.run {
                    KravelApplication.GlobalApp.startActivity(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }

        })
        networkManager.getPlaceList(latitude,longitude,0.025,0.03).safeEnqueue(
            onSuccess = {
                rv_near_place.apply {
                    adapter = placeAdapter
                    addItemDecoration(VerticalItemDecorator(16))
                }

                placeAdapter.initData(it.data!!.result!!.content)
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

    }
}

