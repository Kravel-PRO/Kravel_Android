package com.kravelteam.kravel_android.data.response

data class DetailPlaceResponse(
    val placeId: Int,
    val title: String,
    val imageUrl: String?,
    val mediaTitle: String?,
    val celebrities: ArrayList<String>
)