package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class CelebDetailResponse(
    val celebrity: CelebResponse,
    val places: List<DetailPlaceResponse>
) : Serializable