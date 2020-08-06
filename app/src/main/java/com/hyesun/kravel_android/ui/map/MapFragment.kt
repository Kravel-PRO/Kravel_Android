package com.hyesun.kravel_android.ui.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.HorizontalItemDecorator
import com.hyesun.kravel_android.data.mock.NearPlaceData
import com.hyesun.kravel_android.data.response.DetailPlaceResponse
import com.hyesun.kravel_android.ui.adapter.MapPlaceRecyclerview
import com.hyesun.kravel_android.ui.adapter.PopularRecyclerview
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.core.context.GlobalContext
import timber.log.Timber
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), TMapGpsManager.onLocationChangedCallback {
    private val nearAdapter : MapPlaceRecyclerview by lazy { MapPlaceRecyclerview() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initMap()
        initRecycler()

        togglebtn_gps.setOnClickListener {

        }
    }
    private fun initRecycler() {
        rv_map_near_place.apply {
            adapter = nearAdapter
            addItemDecoration(HorizontalItemDecorator(16))
        }

        nearAdapter.initData(
            listOf(
                NearPlaceData("호텔델루나", arrayListOf("#아이유","#여진구"),"https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호텔델루나 주소"),
                NearPlaceData("호텔델루나", arrayListOf("#아이유","#여진구"),"https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호텔델루나 주소"),
                NearPlaceData("호텔델루나", arrayListOf("#아이유","#여진구"),"https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호텔델루나 주소"),
                NearPlaceData("호텔델루나", arrayListOf("#아이유","#여진구"),"https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호텔델루나 주소"),
                NearPlaceData("호텔델루나", arrayListOf("#아이유","#여진구"),"https://www.dramamilk.com/wp-content/uploads/2019/07/Hotel-de-Luna-episode-5-live-recap-IU.jpg","호텔델루나 주소")

            )
        )
    }
    private fun initMap() {
        val tmap = view?.findViewById<LinearLayout>(R.id.map)
        val tmapView = TMapView(context)
        tmapView.setSKTMapApiKey(getString(R.string.tmap_api_key))
        tmapView.setCompassMode(true)
        tmapView.setIconVisibility(true)
        tmapView.zoomLevel = 15
        tmapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tmapView.mapType = TMapView.MAPTYPE_STANDARD
        tmapView.setTrackingMode(true)
        tmapView.setSightVisible(true)
        tmap?.addView(tmapView)

    }
    private fun initGPS() {
        val tmapgps : TMapGpsManager = TMapGpsManager(context)
        tmapgps.provider= TMapGpsManager.GPS_PROVIDER
        tmapgps.minTime = 1000
        tmapgps.OpenGps()
        Timber.d("${tmapgps.location}")



    }

    override fun onLocationChange(p0: Location?) {
        val lat : Double = p0!!.latitude
        val lon : Double = p0!!.longitude

    }

}
