package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.data.response.BaseResponse
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.data.response.LoginResponse
import com.kravelteam.kravel_android.data.response.MediaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NetworkService {

    /**
     * 로그인
     */
    @POST("/auth/sign-in")
    fun requestLogin(
        @Body data : LoginRequest
    ) : Call<LoginResponse>

    /**
     * 셀럽 리스트
     */
    @GET("/api/articles/celebrities")
    fun requestCelebList() : Call<BaseResponse<List<CelebResponse>>>

    /**
     * 셀럽 상세
     */

    /**
     * 미디어 리스트
     */
    @GET("/api/articles/medias")
    fun requestMediaList() : Call<BaseResponse<List<MediaResponse>>>

}