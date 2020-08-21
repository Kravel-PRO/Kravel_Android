package com.kravelteam.kravel_android.data.response

data class BaseResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null
)