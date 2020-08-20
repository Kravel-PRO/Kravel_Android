package com.hyesun.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.util.onTextChangeListener
import kotlinx.android.synthetic.main.activity_update_info.*

@Suppress("DEPRECATION")
class UpdateInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)

        initCheckRadioBtn()
        initChangeEditText()
        initEnableBtn()
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
                if(!edt_update_info_nickname.text.isNullOrBlank()) edt_update_info_nickname.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_info_nickname.setBackgroundResource(R.drawable.signup_edit_style)
                initEnableBtn()
            }
        )
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
}