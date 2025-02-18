
package com.kravelteam.kravel_android.common

import com.auth0.android.jwt.JWT
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeLoginEnqueue
import com.kravelteam.kravel_android.util.toast
import timber.log.Timber
import java.util.*

fun newToken(authManager: AuthManager,networkManager: NetworkManager): Boolean{
    var result = false
    if(Date(authManager.expire) < Date(System.currentTimeMillis())){ //만료되었을 때
        networkManager.requestRefreshToken().safeLoginEnqueue(
            onSuccess = {
                val jwt = JWT(it)
                val expiresAt = jwt.expiresAt

                authManager.expire = expiresAt?.time!!
                result = true
            },
            onFailure = {
                result = false
            },
            onError = {
                result = false
            }
        )
    } else {
        Timber.e("${Date(authManager.expire)} 안념음 ${Date(System.currentTimeMillis())}")
        result = true
    }

    return result
}