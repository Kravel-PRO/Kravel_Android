package com.hyesun.kravel_android.ui.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hyesun.kravel_android.R
import com.skt.Tmap.TMapView
import org.koin.core.context.GlobalContext
import timber.log.Timber


class MapFragment : Fragment() {

    val apiKey = "l7xx8006432e92764b7bb7edecc3a9a5543c"
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

}