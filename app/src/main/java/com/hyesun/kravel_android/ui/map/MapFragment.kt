package com.hyesun.kravel_android.ui.map

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.hyesun.kravel_android.R
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.core.context.GlobalContext
import timber.log.Timber


class MapFragment : Fragment(), TMapGpsManager.onLocationChangedCallback {

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

        togglebtn_gps.setOnClickListener {
            initGPS()
        }
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
