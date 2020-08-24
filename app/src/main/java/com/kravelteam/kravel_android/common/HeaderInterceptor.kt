package com.kravelteam.kravel_android.common

import com.kravelteam.kravel_android.network.AuthManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor(private val authManager: AuthManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = authManager.token

        val newRequest: Request

        if (token.isBlank()) {
            newRequest = chain.request()
        } else {
            newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }

}