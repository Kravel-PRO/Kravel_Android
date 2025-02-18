package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.common.HeaderInterceptor
import com.kravelteam.kravel_android.data.request.*
import com.kravelteam.kravel_android.data.response.BaseResponse
import com.kravelteam.kravel_android.data.response.MapResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkManager(authManager: AuthManager) {

    private val httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val builder = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(HeaderInterceptor(authManager))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(builder)
        .build()
        .create(NetworkService::class.java)

    private var kakaoRetrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NetworkService::class.java)

    fun requestSearchAddress(
        token: String,
        query: String
    ) = kakaoRetrofit.requestSearchAddress(token,query)

    fun requestReport(
        files : ArrayList<MultipartBody.Part?>,
        title : RequestBody,
        contents : RequestBody,
        address : RequestBody,
        tags : RequestBody,
        inquireCategory: RequestBody
    ) = retrofit.requestReport(files, title, contents, address, tags, inquireCategory)

    fun requestLogin(data : LoginRequest)
            = retrofit.requestLogin(data)

    fun requestSignUp(data: SignUpRequest)
            = retrofit.requestSignUp(data)

    fun requestRefreshToken() = retrofit.requestRefreshToken()

    fun requestUserInfo() = retrofit.requestUserInfo()

    fun requestUpdateInfo(
        type: String,
        data: UpdateInfo
    ) = retrofit.requestUpdateInfo(type, data)

    fun requestSearchResult(
        search: String
    ) = retrofit.requestSearchResult(search)

    fun requestCelebList(
        page : Int,
        size : Int
    ) = retrofit.requestCelebList(page, size)

    fun requestMediaList(
        page : Int,
        size : Int,
        sort : String
    ) = retrofit.requestMediaList(page, size, sort)

    fun requestCelebDetail(
        id: Int,
        page: Int,
        size: Int
    ) = retrofit.requestCelebDetail(id, page, size)

    fun requestMediaDetail(
        id: Int,
        page: Int,
        size: Int
    ) = retrofit.requestMediaDetail(id, page, size)

    fun requestMediaPhotoReview(
        id: Int,
        page : Int,
        size : Int,
        sort : String
    ) = retrofit.requestMediaPhotoReview(id, page, size, sort)

    fun getPlaceList(
        latitude : Double,
        longitude : Double,
        width : Double,
        height : Double
    ) = retrofit.getPlaceList(latitude, longitude,width, height)

    fun getPlaceDetailList(
        placeId : Int
    ) = retrofit.getPlaceDetailList(placeId)

    fun getPopularPlaceList(
        sort : String
    ) = retrofit.getPopularPlaceList(sort)

    fun getPhotoReview(
        page : Int,
        size : Int,
        sort : String
    ) = retrofit.getPhotoReview(page, size, sort)

    fun getCelebPhotoReview(
        id : Int,
        page: Int,
        size: Int,
        sort: String
    ) = retrofit.getCelebPhotoReview(id, page, size, sort)

    fun getPlaceReview(
        placeId : Int,
        page : Int,
        size : Int,
        sort : String
    ) = retrofit.getPlaceReview(placeId, page, size, sort)

    fun requestPostPhotoReview(
        id: Int,
        file: MultipartBody.Part?
    ) = retrofit.requestPostPhotoReview(id,file)

    fun requestDeletePhotoReview(
        id: Int
    ) = retrofit.requestDeletePhotoReview(id)

    fun requestMyScrap() = retrofit.requestMyScrap()

    fun requestMyPhotoReviews(
        page : Int,
        size : Int,
        sort : String
    ) = retrofit.requestMyPhotoReviews(page, size, sort)

    fun getMapMarkerList(
    ) = retrofit.getMapMarkerList()

    fun postScrap(
        placeId: Int,
        data : ScrapBody
    ) = retrofit.postScrap(placeId, data)

    fun postLikes(
        placeId: Int,
        reviewId : Int,
        data : ReviewLikeBody
    ) = retrofit.postLikes(placeId, reviewId, data)

    fun requestLanguage(
        type: String,
        data : LanguageBody
    ) = retrofit.requestLanguage(type, data)

    private companion object {
        const val BASE_URL = "http://15.164.118.217:8080"
//        const val BASE_URL ="http://noah.is.kakaocorp.com"
    }
}

val requestModule = module {
    single { NetworkManager( get() ) }
}