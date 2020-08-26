package com.kravelteam.kravel_android.data.mock

import android.os.Parcelable
import com.kravelteam.kravel_android.data.response.PhotoResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class PlaceInformationData(
    var placeName : String,
    var placeTag : ArrayList<HashTagData>,
    var placeAddress : String,
    var placePhotoReview : ArrayList<PhotoResponse>,
    var marker : LatLng,
    var placeImg : String
) : Parcelable