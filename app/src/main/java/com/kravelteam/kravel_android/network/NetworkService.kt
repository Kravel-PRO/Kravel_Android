package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.data.response.BaseResponse
import com.kravelteam.kravel_android.data.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {

    /**
     * 로그인
     */
    @POST("/auth/sign-in")
    fun requestLogin(
        @Body data : LoginRequest
    ) : Call<LoginResponse>

}