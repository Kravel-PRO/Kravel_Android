package com.kravelteam.kravel_android.data.response

data class PhotoReviewResponse(
    val content : ArrayList<PhotoReviewData>
)
data class PhotoReviewData (
    val reviewId : Int,
    val imageUrl : String,
    val grade : Double,
    val likeCount : Int

)