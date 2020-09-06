package com.kravelteam.kravel_android.data.response

data class PhotoReviewResponse(
    val content : ArrayList<PhotoReviewData>
)
data class PhotoReviewData (
    val reviewId : Int,
    val imageURl : String,
    val likeCount : Int

)