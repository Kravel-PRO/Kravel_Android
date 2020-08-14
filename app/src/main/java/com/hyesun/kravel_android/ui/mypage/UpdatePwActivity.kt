package com.hyesun.kravel_android.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.util.onTextChangeListener
import kotlinx.android.synthetic.main.activity_update_pw.*

class UpdatePwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pw)

        initChangeText()
    }

    private fun initChangeText(){
        edt_update_pw_content.onTextChangeListener(
            onTextChanged = {
                if(!edt_update_pw_content.text.isNullOrBlank()) edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style_true)
                else edt_update_pw_content.setBackgroundResource(R.drawable.signup_edit_style)
            }
        )
    }
}