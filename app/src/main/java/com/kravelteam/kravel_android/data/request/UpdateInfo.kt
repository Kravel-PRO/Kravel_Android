package com.kravelteam.kravel_android.data.request

import java.io.Serializable

data class UpdateInfo(
    val loginPw: String,
    val modifyLoginPw: String,
    val gender: String,
    val nickName: String
) : Serializable