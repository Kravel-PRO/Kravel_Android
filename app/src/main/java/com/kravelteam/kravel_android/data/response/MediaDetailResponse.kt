package com.kravelteam.kravel_android.data.response

data class MediaDetailResponse(
    val mediaId: Int,
    val title: String,
    val imageUrl: String,
    val places: List<DetailPlaceResponse>
)