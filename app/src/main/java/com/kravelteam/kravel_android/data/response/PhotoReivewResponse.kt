package com.kravelteam.kravel_android.data.response

import java.io.Serializable

data class PhotoReviewResponse(
    val content : ArrayList<PhotoReviewData>
)
data class PhotoReviewData (
    val reviewId : Int,
    val imageUrl : String,
    val likeCount : Int,
    var like : Boolean,
    val createdDate : String,
    val place : PhotoPlaceData
) : Serializable

data class PhotoPlaceData (
    val placeId : Int,
    val title : String,
    val tags : String?
) : Serializable