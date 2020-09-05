package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.data.request.SignUpRequest
import com.kravelteam.kravel_android.data.response.*
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    /**
     * 카카오 - 키워드 장소 검색 Api
     */
    @GET("/v2/local/search/keyword.json")
    fun requestSearchAddress(
        @Header("Authorization")  token : String,
        @Query("query") query: String
    ) : Call<AddressResponse>

    /**
     * 로그인
     */
    @POST("/auth/sign-in")
    fun requestLogin(
        @Body data : LoginRequest
    ) : Call<Unit>

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
    @GET("/api/celebrities/{id}")
    fun requestCelebDetail(
        @Path("id") id: Int
    ) : Call<BaseResponse<CelebDetailResponse>>

    /**
     * 셀럽 상세 - 포토 리뷰
     */
    @GET("/api/celebrities/{id}/reviews")
    fun getCelebPhotoReview(
        @Path("id") id : Int
    ) : Call<BaseResponse<CelebPhotoReviewResponse>>

    /**
     * 미디어 리스트
     */
    @GET("/api/medias")
    fun requestMediaList() : Call<BaseResponse<List<MediaResponse>>>

    /**
     * 미디어 상세
     *
     */
    @GET("/api/medias/{id}")
    fun requestMediaDetail(
        @Path("id") id: Int
    ) : Call<BaseResponse<MediaDetailResponse>>

    /**
     * 미디어 상세 - 포토 리뷰
     *
     */
    @GET("/api/medias/{id}/reviews")
    fun requestMediaPhotoReview(
        @Path("id") id: Int
    ) : Call<BaseResponse<CelebPhotoReviewResponse>>

    /**
     * 장소
     */

    /**
     *  장소
     */
    @GET("/api/places")
    fun getPlaceList(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double
    ) : Call<BaseResponse<PlaceDataResponse>>

    /**
     * 장소 상세
     */
    @GET("/api/places/{placeId}")
    fun getPlaceDetailList(
        @Path("placeId") placeId : Int
    ) : Call<BaseResponse<PlaceDetailResponse>>

    /**
     * 인기있는 장소 리스트
     */
    @GET("/api/places")
    fun getPopularPlaceList() : Call<BaseResponse<PlaceDataResponse>>

    /**
     * 장소 상세 - 포토 리뷰
     */
    @GET("/api/places/{placeId}/reviews")
    fun getPlaceReview(
        @Path("placeId") placeId: Int
    ) : Call<BaseResponse<PhotoReviewResponse>>

    /**
     * 포토 리뷰
     */

    /**
     * Home - Photo Review 최신순
     */
    @GET("/api/reviews")
    fun getPhotoReview(
        @Query("offset") offset : Int,
        @Query("size") size : Int,
        @Query("sort") sort : String
    ) : Call<BaseResponse<PhotoReviewResponse>>

}