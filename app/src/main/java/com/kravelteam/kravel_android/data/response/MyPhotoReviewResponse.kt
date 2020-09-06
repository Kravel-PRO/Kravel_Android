package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class MyPhotoReviewResponse(
    val content: List<MyPhotoReviewData>
) : Serializable

data class MyPhotoReviewData(
    val reviewId: Int,
    val title: String,
    val imageUrl: String,
    val year: String,
    val likeCount: Int
) : Serializable