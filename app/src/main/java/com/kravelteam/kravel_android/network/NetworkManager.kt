package com.kravelteam.kravel_android.network

import com.kravelteam.kravel_android.common.HeaderInterceptor
import com.kravelteam.kravel_android.data.request.LoginRequest
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

    private companion object {
        const val BASE_URL = "http://15.164.47.5:8080"
    }
}

val requestModule = module {
    single { NetworkManager( get() ) }
}