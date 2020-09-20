package com.kravelteam.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.UpdateInfo
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_update_info.*
import kotlinx.android.synthetic.main.activity_update_info.lottie_detail_loading
import org.koin.android.ext.android.inject

@Suppress("DEPRECATION")
class UpdateInfoActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private val authManager : AuthManager by inject()
    private lateinit var lottie : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)

        initSetting()
        initCheckEditText()
        initCheckRadioBtn()
        initChangeEditText()
        initEnableBtn()
        initUpdateUserInfo()

        lottie = lottie_detail_loading

        img_update_info_back.setOnDebounceClickListener { finish() }
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

    private fun initSetting(){
        edt_update_info_nickname.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("nickname"))
        val gender = intent.getStringExtra("gender")
        if(gender == "MAN"){
            rb_update_info_man.isChecked = true
            rb_update_info_man.setTextColor(resources.getColor(R.color.colorCoral))
        } else {
            rb_update_info_woman.isChecked = true
            rb_update_info_woman.setTextColor(resources.getColor(R.color.colorCoral))
        }
    }

    private fun initCheckRadioBtn(){
        rb_update_info_man.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_update_info_man.setTextColor(resources.getColor(R.color.colorCoral))
            } else {
                rb_update_info_man.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }

        rb_update_info_woman.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_update_info_woman.setTextColor(resources.getColor(R.color.colorCoral))
            } else {
                rb_update_info_woman.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }
    }

    private fun initChangeEditText(){
        edt_update_info_nickname.onTextChangeListener(
            onTextChanged = {
                initCheckEditText()
                initEnableBtn()
            }
        )
    }

    private fun initCheckEditText(){
        if(!edt_update_info_nickname.text.isNullOrBlank()) edt_update_info_nickname.setBackgroundResource(R.drawable.signup_edit_style_true)
        else edt_update_info_nickname.setBackgroundResource(R.drawable.signup_edit_style)
    }

    private fun initEnableBtn(){
        if(!edt_update_info_nickname.text.isNullOrBlank()){
            btn_update_info_complete.isEnabled = true
            btn_update_info_complete.setTextColor(Color.WHITE)
        } else {
            btn_update_info_complete.isEnabled = false
            btn_update_info_complete.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }

    private fun initUpdateUserInfo(){
        btn_update_info_complete.setOnDebounceClickListener {
            if(edt_update_info_nickname.text.length>8){
                toast(resources.getString(R.string.hintNickname))
            } else {
                onLoading()
                val gender = if(rb_update_info_man.isChecked){
                    "MAN"
                } else "WOMAN"

                val data = UpdateInfo(
                    "",
                    "",
                    gender,
                    edt_update_info_nickname.text.toString()
                )
                if (newToken(authManager,networkManager)) {
                    networkManager.requestUpdateInfo("nickNameAndGender", data).safeEnqueue(
                        onSuccess = {
                            finish()
                            toast(resources.getString(R.string.successUpdate))
                        },
                        onFailure = {
                            if(it.code() == 403) {
                                toast(resources.getString(R.string.errorReLogin))
                            } else {
                                toast(resources.getString(R.string.errorClient))
                            }
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

    override fun onDestroy() {
        super.onDestroy()
        offLoading()
    }
}