package com.kravelteam.kravel_android.data.response

data class PlaceDetailResponse(
    val placeId : Int,
    val title : String,
    val contents : String?,
    val imageUrl : String?,
    val location : String,
    val latitude : Double,
    val longitude : Double,
    val bus : String,
    val subway : String?,
    var scrap : Boolean,
    val mediaId : Int,
    val mediaTitle : String,
    val reviewCount : Int,
    val tags : String?,
    val celebrities : ArrayList<PlaceCelebResponse>
)