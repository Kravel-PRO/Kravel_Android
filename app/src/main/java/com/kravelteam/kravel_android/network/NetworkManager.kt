package com.kravelteam.kravel_android.network

import android.gesture.OrientedBoundingBox
import com.kravelteam.kravel_android.common.HeaderInterceptor
import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.data.request.SignUpRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
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

    fun requestLogin(data : LoginRequest)
            = retrofit.requestLogin(data)

    fun requestSignUp(data: SignUpRequest)
            = retrofit.requestSignUp(data)

    fun requestCelebList() = retrofit.requestCelebList()

    fun requestMediaList() = retrofit.requestMediaList()

    fun getPlaceList(
        latitude : Double,
        longitude : Double
    ) = retrofit.getPlaceList(latitude, longitude)

    fun getPlaceDetailList(
        placeId : Int
    ) = retrofit.getPlaceDetailList(placeId)

    fun getPopularPlaceList() = retrofit.getPopularPlaceList()

    private companion object {
        const val BASE_URL = "http://15.164.118.217:8080"
    }
}

val requestModule = module {
    single { NetworkManager( get() ) }
}