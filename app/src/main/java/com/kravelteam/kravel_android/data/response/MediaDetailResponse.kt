package com.kravelteam.kravel_android.data.response

data class MediaDetailResponse(
    val media: MediaResponse,
    val places: List<DetailPlaceResponse>
)