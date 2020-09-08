package com.kravelteam.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.UpdateInfo
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_update_pw.*
import org.koin.android.ext.android.inject

class UpdatePwActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pw)

        initChangeEditText()
        initEnableBtn()
        initUpdatePw()

        img_update_pw_back.setOnDebounceClickListener {
            finish()
        }
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
                    if(edt_update_pw_change_check.text == edt_update_pw_change.text){
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
            val data = UpdateInfo(
                edt_update_pw_content.text.toString(),
                edt_update_pw_change_check.text.toString(),
                "",
                "")
            networkManager.requestUpdateInfo("password",data).safeEnqueue(
                onSuccess = {
                    toast("성공")
                },
                onFailure = {
                    toast("실패")
                },
                onError = {
                    toast("에러")
                }
            )
        }
    }
}