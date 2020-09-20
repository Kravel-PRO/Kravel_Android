package com.kravelteam.kravel_android.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import com.airbnb.lottie.LottieAnimationView
import com.auth0.android.jwt.JWT
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.LoginRequest
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.main.MainActivity
import com.kravelteam.kravel_android.ui.signup.SignUpActivity
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.lottie_detail_loading
import kotlinx.android.synthetic.main.dialog_login_fail.view.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LoginActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    private var showCheck: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lottie = lottie_detail_loading
        init()
    }

    private fun onLoading(){
        lottie.apply {
            setAnimation("heart_loading.json")
            playAnimation()
            loop(true)
        }
        lottie_background.setVisible()
        lottie_detail_loading.setVisible()
    }

    private fun offLoading(){
        lottie_background.setGone()
        lottie_detail_loading.setGone()
    }

    //초기 설정
    private fun init(){

        btn_login.setOnClickListener {
            showCheck = true
            afterAnimation()
        }

        btn_signup.setOnDebounceClickListener {
            val intent = Intent(this,
                SignUpActivity::class.java)
            startActivity(intent)
        }

        img_login_btn_back.setOnClickListener {
            if(showCheck) {
                beforeAnimation()
                showCheck = false
            }
        }

        btn_login_other_view.setOnDebounceClickListener {
            onLoading()
            val loginData = LoginRequest(
                loginEmail = edt_login_email.text.toString(),
                loginPw = edt_login_pw.text.toString()
            )
            networkManager.requestLogin(loginData).safeLoginEnqueue(
                onSuccess = {
                    val token = it
                    authManager.apply {
                        this.token = token
                        autoLogin = true
                    }
                    authManager.expire = JWT(token).expiresAt?.time!!
                    Timber.e("${Date(JWT(token).expiresAt?.time!!)}")
                    startActivity(MainActivity::class,true)
                },
                onFailure = {
                    if(it.code() == 400){
                        initDialog()
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        }

        textChange()
    }

    //애니메이션 효과 -> after
    private fun afterAnimation(){
        cl_login_btn_space.visibility = View.GONE
        cl_login_edt_space.visibility = View.VISIBLE
        cl_login_title_space.visibility = View.VISIBLE

        val fade = ObjectAnimator.ofFloat(txt_login_title,View.ALPHA,0.0f,1.0f)
        val fade2 = ObjectAnimator.ofFloat(txt_login_title1, View.ALPHA, 0.0f, 1.0f)
        val ty1 = ObjectAnimator.ofFloat(cl_login_edt_space, View.TRANSLATION_Y, 1250f, 0f)
        val ty2 = ObjectAnimator.ofFloat(cl_img_background, View.TRANSLATION_Y, 0f, -700f)
        AnimatorSet().run {
            playTogether(fade, fade2, ty1, ty2)
            duration = 1000
            start()
        }
    }

    //애니메이션 효과 -> before
    private fun beforeAnimation(){
        val anim = AnimatorSet()
        val fade = ObjectAnimator.ofFloat(txt_login_title,View.ALPHA,1.0f,0.0f)
        val fade2 = ObjectAnimator.ofFloat(txt_login_title1,View.ALPHA,1.0f,0.0f)
        val ty1 = ObjectAnimator.ofFloat(cl_login_edt_space, View.TRANSLATION_Y, 0f, 1250f)
        val ty2 = ObjectAnimator.ofFloat(cl_img_background, View.TRANSLATION_Y, -700f, 0f)
        anim.run {
            playTogether(fade,fade2, ty1,ty2)
            duration = 1000
            start()

        }

        anim.doOnEnd {
            cl_login_edt_space.visibility = View.GONE
            cl_login_title_space.visibility = View.GONE
            cl_login_btn_space.visibility = View.VISIBLE
        }
    }

    //텍스트 변화
    private fun textChange(){
        //email text change
        edt_login_email.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailTextChangeBackground()
                loginBtnEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                emailTextChangeBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        //pw text change
        edt_login_pw.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pwTextChangeBackground()
                loginBtnEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                pwTextChangeBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    private fun emailTextChangeBackground(){
        if(!edt_login_email.text.isNullOrBlank()) edt_login_email.setBackgroundResource(R.drawable.signup_edit_style_true)
        else edt_login_email.setBackgroundResource(R.drawable.grey_round_background)
    }

    private fun pwTextChangeBackground(){
        if(!edt_login_pw.text.isNullOrBlank()) edt_login_pw.setBackgroundResource(R.drawable.signup_edit_style_true)
        else edt_login_pw.setBackgroundResource(R.drawable.grey_round_background)
    }

    private fun loginBtnEnable(){
        if(!edt_login_email.text.isNullOrBlank() and !edt_login_pw.text.isNullOrBlank()){
            btn_login_other_view.isEnabled = true
            btn_login_other_view.setTextColor(Color.WHITE)
        }else{
            btn_login_other_view.isEnabled = false
            btn_login_other_view.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }

    private fun initDialog(){
        val dialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_login_fail,null)

        view.txt_login_fail.text = resources.getString(R.string.loginFailTitle)
        view.txt_login_fail_content.text = resources.getString(R.string.loginFailContent)

        view.btn_dialog_retry.setOnDebounceClickListener {
            dialog.cancel()
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        offLoading()
    }
}
