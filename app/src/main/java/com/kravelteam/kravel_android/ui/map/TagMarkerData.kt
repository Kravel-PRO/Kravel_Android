package com.kravelteam.kravel_android.ui.map

import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay

data class TagMarkerData(
    val placeId: Int,
    var markerClick: Boolean = false
)