package com.kravelteam.kravel_android.network

import android.content.Context
import androidx.core.content.edit
import org.koin.dsl.module

class AuthManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        AUTH_PREFERENCES,
        Context.MODE_PRIVATE
    )

    var token: String
        get() {
            return preferences.getString(TOKEN_KEY, null).orEmpty()
        }
        set(value) {
            preferences.edit {
                putString(TOKEN_KEY, value)
            }
        }

    var autoLogin: Boolean
        get() {
            return preferences.getBoolean(AUTO_LOGIN_KEY, false)
        }
        set(value) {
            preferences.edit {
                putBoolean(AUTO_LOGIN_KEY, value)
            }
        }


    private companion object {
        const val AUTH_PREFERENCES = "auth"
        const val TOKEN_KEY = "token"
        const val AUTO_LOGIN_KEY = "auto"
    }
}

val authModule = module {
    single { AuthManager(get()) }
}