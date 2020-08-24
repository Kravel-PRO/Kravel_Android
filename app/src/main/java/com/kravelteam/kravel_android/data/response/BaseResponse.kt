package com.kravelteam.kravel_android.data.response

data class BaseResponse<T>(
    val status: Boolean,
    val message: String,
    val timestamp: String,
    val data: BaseResponseData<T>,
    val error: ErrorBody? = null
)

data class BaseResponseData<T>(
    val result: T
)

data class ErrorBody(
    val code: Int,
    val errorMessage: String,
    val referedUrl: String
)