package com.kravelteam.kravel_android.common

import android.content.Context
import android.widget.Toast
import com.auth0.android.jwt.JWT
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeLoginEnqueue
import com.kravelteam.kravel_android.util.toast
import timber.log.Timber
import java.util.*

fun newToken(authManager: AuthManager,networkManager: NetworkManager){
    if(Date(authManager.expire) < Date(System.currentTimeMillis())){ //만료되었을 때
        networkManager.requestRefreshToken().safeLoginEnqueue(
            onSuccess = {
                val jwt = JWT(it)
                val expiresAt = jwt.expiresAt

                authManager.expire = expiresAt?.time!!
            },
            onFailure = {
                KravelApplication.GlobalApp.toast("재로그인")
            },
            onError = {
                KravelApplication.GlobalApp.networkErrorToast()
            }
        )
    } else {
        Timber.e("안넘음")
    }
}