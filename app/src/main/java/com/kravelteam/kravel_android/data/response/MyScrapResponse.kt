package com.kravelteam.kravel_android.data.response

import java.io.Serializable


data class MyScrapResponse(
    val content: List<MyScrapData>
): Serializable

data class MyScrapData(
    val placeId: Int,
    val title: String,
    val imageUrl: String
): Serializable
