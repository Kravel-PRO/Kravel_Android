package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class CelebListResponse(
    val content: List<CelebResponse>
): Serializable

data class CelebResponse(
    val celebrityId: Int,
    val celebrityName: String,
    val imageUrl: String?
): Serializable