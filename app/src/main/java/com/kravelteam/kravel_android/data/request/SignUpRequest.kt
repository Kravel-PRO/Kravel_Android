package com.kravelteam.kravel_android.data.request

data class SignUpRequest(
    val loginEmail: String,
    val loginPw: String,
    val nickName: String,
    val gender: String,
    val speech: String
)