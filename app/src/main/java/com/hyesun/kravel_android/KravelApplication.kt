package com.hyesun.kravel_android

import android.app.Application
import com.hyesun.kravel_android.network.requestModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class KravelApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        GlobalApp = this

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@KravelApplication)
            modules(
                listOf(requestModule)
            )
        }
    }

    companion object {
        lateinit var GlobalApp : KravelApplication
    }
}