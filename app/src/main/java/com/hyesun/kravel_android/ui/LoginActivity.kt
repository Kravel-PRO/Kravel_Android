package com.hyesun.kravel_android.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationSet
import androidx.core.animation.doOnEnd
import com.hyesun.kravel_android.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var showCheck: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    //초기 설정
    private fun init(){
        btn_login.setOnClickListener {
            showCheck = true
            afterAnimation()
        }
        cl_login_title_space.setOnClickListener {
            if(showCheck) {
                beforeAnimation()
                showCheck = false
            }
        }
    }

    //애니메이션 효과 -> after
    private fun afterAnimation(){
        cl_login_btn_space.visibility = View.GONE
        cl_login_edt_space.visibility = View.VISIBLE
        cl_login_title_space.visibility = View.VISIBLE

        val fade = ObjectAnimator.ofFloat(txt_login_title,View.ALPHA,0.0f,1.0f)
        val ty1 = ObjectAnimator.ofFloat(cl_login_edt_space, View.TRANSLATION_Y, 1250f, 0f)
        val ty2 = ObjectAnimator.ofFloat(cl_img_background, View.TRANSLATION_Y, 0f, -700f)
        AnimatorSet().run {
            playTogether(fade,ty1,ty2)
            duration = 1000
            start()
        }
    }

    //애니메이션 효과 -> before
    private fun beforeAnimation(){
        val anim = AnimatorSet()
        val fade = ObjectAnimator.ofFloat(txt_login_title,View.ALPHA,1.0f,0.0f)
        val ty1 = ObjectAnimator.ofFloat(cl_login_edt_space, View.TRANSLATION_Y, 0f, 1250f)
        val ty2 = ObjectAnimator.ofFloat(cl_img_background, View.TRANSLATION_Y, -700f, 0f)
        anim.run {
            playTogether(fade,ty1,ty2)
            duration = 1000
            start()

        }

        anim.doOnEnd {
            cl_login_edt_space.visibility = View.GONE
            cl_login_title_space.visibility = View.GONE
            cl_login_btn_space.visibility = View.VISIBLE
        }
    }
}
