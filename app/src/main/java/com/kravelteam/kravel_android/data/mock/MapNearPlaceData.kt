package com.kravelteam.kravel_android.data.mock

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapNearPlaceData (
    var imgPlace : String?,
    var txtPlace : String?
) : Parcelable