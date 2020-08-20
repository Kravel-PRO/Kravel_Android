package com.kravelteam.kravel_android.ui.signup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import kotlinx.android.synthetic.main.activity_set_language.*

@Suppress("DEPRECATION")
class SetLanguageActivity : AppCompatActivity() {

    private var checkLang = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

        initCheckRadioBtn()
        initEnableBtn()
    }

    private fun initCheckRadioBtn(){
        rb_set_language_korea.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                initEnableBtn()
            } else {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }

        rb_set_language_chinese.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_chinese.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                initEnableBtn()
            } else {
                rb_set_language_chinese.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }

        rb_set_language_english.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                initEnableBtn()
            } else {
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }
    }

    private fun initEnableBtn(){
        if(checkLang) {
            btn_set_language_start.isEnabled = true
            btn_set_language_start.setTextColor(Color.WHITE)
        } else {
            btn_set_language_start.isEnabled = false
            btn_set_language_start.setTextColor(resources.getColor(R.color.colorDarkGrey))
        }
    }
}