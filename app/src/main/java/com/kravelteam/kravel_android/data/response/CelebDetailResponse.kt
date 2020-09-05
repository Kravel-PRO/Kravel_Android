package com.kravelteam.kravel_android.data.response

data class CelebDetailResponse(
    val celebrity: CelebResponse,
    val places: List<DetailPlaceResponse>
)