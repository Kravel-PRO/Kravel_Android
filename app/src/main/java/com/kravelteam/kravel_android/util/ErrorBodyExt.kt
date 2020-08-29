package com.kravelteam.kravel_android.util

import com.google.gson.Gson
import com.kravelteam.kravel_android.data.response.BaseResponse
import okhttp3.ResponseBody

fun ResponseBody?.toJson() : BaseResponse<*>
        = Gson().fromJson(this?.string(), BaseResponse::class.java)