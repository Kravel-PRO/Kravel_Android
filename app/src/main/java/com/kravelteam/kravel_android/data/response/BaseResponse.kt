package com.kravelteam.kravel_android.data.response

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val timestamp: String,
    val data: T? = null,
    val error: ErrorBody? = null
)

data class ErrorBody(
    val code: Int,
    val errorMessage: String,
    val referedUrl: String
)