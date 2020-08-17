package com.hyesun.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.setOnDebounceClickListener
import com.hyesun.kravel_android.util.onTextChangeListener
import com.hyesun.kravel_android.util.setInvisible
import com.hyesun.kravel_android.util.setVisible
import com.hyesun.kravel_android.util.startActivity
import kotlinx.android.synthetic.main.activity_update_pw.*

class UpdatePwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pw)

        initCheckPersonal()
        initChangeEditText()
        initEnableBtn()
    }

    private fun initCheckPersonal(){
        btn_update_pw_check.setOnDebounceClickListener {
            cl_update_pw_content.setInvisible()
            cl_update_pw_content2.setVisible()
        }
    }

    private fun initChangeEditText(){
        edt_update_pw_content.onTextChangeListener(
            onTextChanged = {
                if(!edt_update_pw_content.text.isNullOrBlank()) edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style)
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
                if(!edt_update_pw_change_check.text.isNullOrBlank()) edt_update_pw_change_check.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_pw_change_check.setBackgroundResource(R.drawable.signup_edit_style)
                initEnableBtn()
            }
        )
    }

    private fun initEnableBtn(){
        if(!edt_update_pw_change.text.isNullOrBlank() and !edt_update_pw_change_check.text.isNullOrBlank()){
            btn_update_pw_complete.isEnabled = true
            btn_update_pw_complete.setTextColor(Color.WHITE)
        } else {
            btn_update_pw_complete.isEnabled = false
            btn_update_pw_complete.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }
}