package com.kravelteam.kravel_android.ui.map

import com.naver.maps.map.overlay.Marker

data class MarkerClick(
    val markerId : Int,
    val marker : Marker,
    var markerClick : Boolean = false
)