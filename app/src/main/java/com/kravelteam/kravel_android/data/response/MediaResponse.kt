package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class MediaResponse(
    val mediaId: Int,
    val title: String,
    val imageUrl: String,
    val year: String
): Serializable