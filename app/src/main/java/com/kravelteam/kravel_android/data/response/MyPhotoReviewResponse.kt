package com.kravelteam.kravel_android.data.response

data class MyPhotoReviewResponse(
    val content: List<MyPhotoReviewData>
)

data class MyPhotoReviewData(
    val reviewId: Int,
    val title: String,
    val imageUrl: String,
    val year: String,
    val likeCount: Int
)