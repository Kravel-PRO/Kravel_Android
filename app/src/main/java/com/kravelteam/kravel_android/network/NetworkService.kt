package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.data.request.SignUpRequest
import com.kravelteam.kravel_android.data.response.*
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
    ) : Call<BaseResponse<LoginResponse>>

    /**
     * 회원가입
     */
    @POST("/auth/sign-up")
    fun requestSignUp(
        @Body data: SignUpRequest
    ) : Call<BaseResponse<Int>>

    /**
     * 셀럽 리스트
     */
    @GET("/api/celebrities")
    fun requestCelebList() : Call<BaseResponse<List<CelebResponse>>>

    /**
     * 셀럽 상세
     */

    /**
     * 미디어 리스트
     */
    @GET("/api/medias")
    fun requestMediaList() : Call<BaseResponse<List<MediaResponse>>>

}