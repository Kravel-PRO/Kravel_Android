package com.kravelteam.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.UpdateInfo
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_update_pw.*
import kotlinx.android.synthetic.main.activity_update_pw.lottie_detail_loading
import kotlinx.android.synthetic.main.dialog_login_fail.view.*
import org.koin.android.ext.android.inject

class UpdatePwActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private val authManager: AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pw)

        initChangeEditText()
        initEnableBtn()
        initUpdatePw()

        lottie = lottie_detail_loading

        img_update_pw_back.setOnDebounceClickListener {
            finish()
        }
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
        lottie_detail_loading.setGone()
        lottie_background.setGone()
    }

    private fun initChangeEditText(){
        edt_update_pw_content.onTextChangeListener(
            onTextChanged = {
                if(!edt_update_pw_content.text.isNullOrBlank()) edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style)
                initEnableBtn()
            }
        )

        edt_update_pw_change.onTextChangeListener(
            onTextChanged = {
                if(!edt_update_pw_change.text.isNullOrBlank()) edt_update_pw_change.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_pw_change.setBackgroundResource(R.drawable.signup_edit_style)
                initEnableBtn()
            }
        )

        edt_update_pw_change_check.onTextChangeListener(
            onTextChanged = {
                if(!edt_update_pw_change_check.text.isNullOrBlank()) {
                    if(edt_update_pw_change_check.text.toString() == edt_update_pw_change.text.toString()){
                        edt_update_pw_change_check.setBackgroundResource(R.drawable.signup_edit_style_true)
                    } else {
                        edt_update_pw_change_check.setBackgroundResource(R.drawable.signup_edit_style)
                    }
                }
                else edt_update_pw_change_check.setBackgroundResource(R.drawable.signup_edit_style)
                initEnableBtn()
            }
        )
    }

    private fun initEnableBtn(){
        if(!edt_update_pw_content.text.isNullOrBlank() and !edt_update_pw_change.text.isNullOrBlank() and !edt_update_pw_change_check.text.isNullOrBlank()){
            btn_update_pw_complete.isEnabled = true
            btn_update_pw_complete.setTextColor(Color.WHITE)
        } else {
            btn_update_pw_complete.isEnabled = false
            btn_update_pw_complete.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }

    private fun initUpdatePw(){
        btn_update_pw_complete.setOnDebounceClickListener {
            if(edt_update_pw_change.text.length < 6){
                toast(resources.getString(R.string.hintPw))
            } else {
                onLoading()
                val data = UpdateInfo(
                    edt_update_pw_content.text.toString(),
                    edt_update_pw_change_check.text.toString(),
                    "",
                    "")
                if (newToken(authManager,networkManager)) {
                    networkManager.requestUpdateInfo("password",data).safeEnqueue(
                        onSuccess = {
                            finish()
                            toast(resources.getString(R.string.successUpdate))
                        },
                        onFailure = {
                            when {
                                it.code() == 403 -> {
                                    toast(resources.getString(R.string.errorReLogin))
                                }
                                it.code() == 400 -> {
                                    initDialog()
                                }
                                else -> {
                                    toast(resources.getString(R.string.errorClient))
                                }
                            }
                            offLoading()
                        },
                        onError = {
                            networkErrorToast()
                            offLoading()
                        }
                    )
                } else {
                    toast(resources.getString(R.string.errorNetwork))
                    offLoading()
                }
            }

        }
    }

    private fun initDialog(){
        val dialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_login_fail,null)

        view.txt_login_fail.text = resources.getString(R.string.authFail)
        view.txt_login_fail_content.text = resources.getString(R.string.authFailCheckPw)

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