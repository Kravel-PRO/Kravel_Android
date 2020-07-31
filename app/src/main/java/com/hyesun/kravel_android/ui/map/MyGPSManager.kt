package com.hyesun.kravel_android.ui.map

import android.app.Activity
import android.content.Context
import android.location.Location
import com.hyesun.kravel_android.KravelApplication.Companion.GlobalApp
import com.skt.Tmap.TMapGpsManager
import org.koin.core.context.GlobalContext

class MyGPSManager() : TMapGpsManager.onLocationChangedCallback {

    private val tmapgps : TMapGpsManager = TMapGpsManager(GlobalApp)
    override fun onLocationChange(p0: Location?) {
        val lat : Double = p0!!.latitude
        val lon : Double = p0!!.longitude

    }
}