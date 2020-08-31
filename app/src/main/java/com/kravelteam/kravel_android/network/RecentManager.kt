package com.kravelteam.kravel_android.network

import android.content.Context
import com.google.gson.JsonArray
import org.koin.dsl.module

class RecentManager(context: Context) {
    private val preferences = context.getSharedPreferences(
        RECENT_PREFERENCES,
        Context.MODE_PRIVATE
    )

    var recentList = JsonArray()

    companion object {
        private const val RECENT_PREFERENCES = "recent"
    }
}

val recentModule = module {
    single {
        RecentManager(get())
    }
}
