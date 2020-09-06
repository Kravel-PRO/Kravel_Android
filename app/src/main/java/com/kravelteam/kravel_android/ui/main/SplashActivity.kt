package com.kravelteam.kravel_android.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.ui.login.LoginActivity
import com.kravelteam.kravel_android.util.startActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.ext.android.inject

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

        Handler().postDelayed({
            if(authManager.autoLogin){
                startActivity(MainActivity::class,true)
            } else {
                startActivity(LoginActivity::class,true)
            }
        },1500)
    }
}