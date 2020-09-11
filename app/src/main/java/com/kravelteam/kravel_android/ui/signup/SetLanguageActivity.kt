package com.kravelteam.kravel_android.ui.signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.AuthManager
import kotlinx.android.synthetic.main.activity_set_language.*
import org.koin.android.ext.android.inject
import java.util.*

@Suppress("DEPRECATION")
class SetLanguageActivity : AppCompatActivity() {
    private val authManager : AuthManager by inject()
    private var checkLang = false
    private var Kor = false
    private var Eng = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)
        btn_set_language_start.setOnDebounceClickListener {
            if(Kor) {
                val ko = Locale.KOREA
                val config = Configuration()
                config.locale = ko
                authManager.setLang = "ko"
                resources.updateConfiguration(config,resources.displayMetrics)
                val intent = baseContext.packageManager.getLaunchIntentForPackage(
                    baseContext.packageName
                )
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intent)


            }
            if(Eng) {
                val en = Locale.US
                val config = Configuration()
                config.locale = en
                authManager.setLang = "en"
                resources.updateConfiguration(config,resources.displayMetrics)
                val intent = baseContext.packageManager.getLaunchIntentForPackage(
                    baseContext.packageName
                )
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intent)
            }


        }

        initCheckRadioBtn()
        initEnableBtn()
    }

    private fun initCheckRadioBtn(){
        rb_set_language_korea.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                Kor = true
                initEnableBtn()
            } else {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }

        rb_set_language_english.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                Eng = true
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