package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class CelebResponse(
    val celebrityId: Int,
    val celebrityName: String,
    val imgUrl: String?
): Serializable