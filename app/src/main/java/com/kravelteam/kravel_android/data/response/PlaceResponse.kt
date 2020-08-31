package com.kravelteam.kravel_android.data.response


data class PlaceDataResponse (
    val content : ArrayList<PlaceContentResponse>
)
data class PlaceContentResponse(
    val placeId : Int,
    val title : String,
    val imageUrl : String?,
    val subImageUrl : String?,
    val location : String,
    val latitude : Double,
    val longitude : Double,
    val reviewCount : Int,
    val tags : Array<String>?,
    val celebrities : Array<PlaceCelebResponse>
)
data class PlaceCelebResponse(
    val celebrityId : Int,
    val celebrityName : String,
    val imageUrl : String
)