package com.kravelteam.kravel_android.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var splashView : LottieAnimationView

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

        },3000)
    }
}