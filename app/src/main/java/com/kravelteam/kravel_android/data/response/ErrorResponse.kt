package com.kravelteam.kravel_android.data.response

data class ErrorResponse(
    val timestamp: Int,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)