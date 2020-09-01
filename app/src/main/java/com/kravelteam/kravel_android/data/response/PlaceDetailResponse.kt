package com.kravelteam.kravel_android.data.response

data class PlaceDetailResponse(
    val placeId : Int,
    val title : String,
    val contents : String,
    val imageUrl : String,
    val location : String,
    val latitude : Double,
    val longitude : Double,
    val grade : Double,
    val weight : Double,
    val mediaId : Int,
    val mediaName : String,
    val reviewCount : Int,
    val celebrities : Array<PlaceCelebResponse>
)