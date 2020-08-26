package com.kravelteam.kravel_android.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoResponse(
    val img: String
) : Parcelable