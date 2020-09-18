package com.kravelteam.kravel_android.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.ui.login.LoginActivity
import com.kravelteam.kravel_android.ui.signup.SetLanguageActivity
import com.kravelteam.kravel_android.util.startActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var splashView : LottieAnimationView
    private val authManager : AuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashView = lottie_splash

        splashView.apply {
            setAnimation("splash_android.json")
            playAnimation()
            loop(true)
        }


        val locale = resources.configuration.locale
        Timber.e("setting locale ::::: ${locale.language}")
        if(!authManager.setLang.isNullOrEmpty()) {
            if(authManager.setLang=="KOR") {
                val kor = Locale.KOREA
                val config = Configuration()
                config.locale = kor
                authManager.setLang = "KOR"
                resources.updateConfiguration(config,resources.displayMetrics)
            }
            if(authManager.setLang =="ENG") {
                val en = Locale.US
                val config = Configuration()
                config.locale = en
                authManager.setLang = "ENG"
                resources.updateConfiguration(config,resources.displayMetrics)
            }
        }
        Handler().postDelayed({
            if(authManager.first){
                if(authManager.autoLogin){
                    startActivity(MainActivity::class,true)
                } else {
                    startActivity(LoginActivity::class,true)
                }
            } else {
                startActivity(SetLanguageActivity::class,true)
            }
        },1200)
    }
}