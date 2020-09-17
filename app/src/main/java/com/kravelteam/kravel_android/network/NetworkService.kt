package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.data.request.*
import com.kravelteam.kravel_android.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    /**
     * 카카오 - 키워드 장소 검색 Api
     */
    @GET("/v2/local/search/keyword.json")
    fun requestSearchAddress(
        @Header("Authorization") token : String,
        @Query("query") query: String
    ) : Call<AddressResponse>

    /**
     * 제보하기
     */
    @Multipart
    @POST("/api/member/inquires")
    fun requestReport(
        @Part files : ArrayList<MultipartBody.Part?>,
        @Part("title") title: RequestBody,
        @Part("contents") contents: RequestBody,
        @Part("address") address: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part("inquireCategory") inquireCategory: RequestBody
    ) : Call<BaseResponse<Int>>

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
     * 내 정보 가져오기
     */
    @GET("/api/members/me")
    fun requestUserInfo() : Call<BaseResponse<UserInfoResponse>>

    /**
     * 내 정보 수정/비밀번호 수정
     */
    @PUT("/api/member")
    fun requestUpdateInfo(
        @Query("type") type: String,
        @Body data: UpdateInfo
    ) : Call<BaseResponse<Unit>>

    /**
     * 셀럽/미디어 검색 결과
     */
    @GET("/api/search")
    fun requestSearchResult(
        @Query("search") search: String
    ) : Call<BaseResponse<SearchResultResponse>>

    /**
     * 셀럽 리스트
     */
    @GET("/api/celebrities")
    fun requestCelebList() : Call<BaseResponse<CelebListResponse>>

    /**
     * 셀럽 상세
     */
    @GET("/api/celebrities/{id}")
    fun requestCelebDetail(
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : Call<BaseResponse<CelebDetailResponse>>

    /**
     * 셀럽 상세 - 포토 리뷰
     */
    @GET("/api/celebrities/{id}/reviews")
    fun getCelebPhotoReview(
        @Path("id") id : Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ) : Call<BaseResponse<PhotoReviewResponse>>

    /**
     * 미디어 리스트
     */
    @GET("/api/medias")
    fun requestMediaList() : Call<BaseResponse<MediaListResponse>>

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
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ) : Call<BaseResponse<PhotoReviewResponse>>

    /**
     * 장소
     */

    /**
     * 지도 마커
     */
    @GET("/api/places/map")
    fun getMapMarkerList(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double
    ) : Call<BaseResponse<List<MapResponse>>>

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
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("sort") sort : String
    ) : Call<BaseResponse<PhotoReviewResponse>>

    /**
     * Home - Photo Review Detail - 좋아요
     */
    @POST("/api/places/{placeId}/reviews/{reviewId}/likes")
    fun postLikes(
        @Path("placeId") placeId : Int,
        @Path("reviewId") reviewId : Int,
        @Body data : ReviewLikeBody
    ) : Call<BaseResponse<Int>>

    /**
     * 장소에 대한 포토 리뷰 작성하기
     */
    @Multipart
    @POST("/api/places/{placeId}/reviews")
    fun requestPostPhotoReview(
        @Path("placeId") id: Int,
        @Part file: MultipartBody.Part?
    ) : Call<BaseResponse<Int>>

    /**
     * 내 스크랩 정보
     */
    @GET("/api/member/scraps")
    fun requestMyScrap() : Call<BaseResponse<MyScrapResponse>>

    /**
     * 내 포토 리뷰
     */
    @GET("/api/member/reviews")
    fun requestMyPhotoReviews() : Call<BaseResponse<PhotoReviewResponse>>

    /**
     * 장소 상세 - 스크랩
     */
    @POST("/api/places/{placeId}/scrap")
    fun postScrap(
        @Path("placeId") placeId : Int,
        @Body data : ScrapBody
    ) : Call<BaseResponse<Int>>

    /**
     * 정보수정 -> 언어
     */
    @PUT("/api/member")
    fun requestLanguage(
        @Query("type") type : String,
        @Body data : LanguageBody
    ) : Call<BaseResponse<Unit>>

}