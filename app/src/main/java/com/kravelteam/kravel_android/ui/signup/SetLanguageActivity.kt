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
import com.kravelteam.kravel_android.data.request.LanguageBody
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.login.LoginActivity
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeLoginEnqueue
import com.kravelteam.kravel_android.util.startActivity
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_set_language.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

@Suppress("DEPRECATION")
class SetLanguageActivity : AppCompatActivity() {
    private val authManager : AuthManager by inject()
    private var checkLang = false
    private var Kor = false
    private var Eng = false
    private val networkManager : NetworkManager by inject()
    private lateinit var configuration: Configuration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

        intent.getStringExtra("my")?.let {
            if(authManager.setLang == "KOR"){
                rb_set_language_korea.isChecked = true
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorCoral))
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorDarkGrey))
                checkLang=true
            } else {
                rb_set_language_english.isChecked = true
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorDarkGrey))
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang=true
            }
            btn_set_language_start.text = resources.getString(R.string.updateComplete)
        }

        configuration = Configuration()
        btn_set_language_start.setOnDebounceClickListener {
            if(intent.getStringExtra("my")=="my"){
                if(Kor) {
                    val ko = Locale.KOREA
                    configuration.locale = ko
                    requestServer("KOR")
                    authManager.setLang = "KOR"
                }
                if(Eng) {
                    val en = Locale.US
                    configuration.locale = en
                    requestServer("ENG")
                    authManager.setLang = "ENG"
                }
            } else {
                startActivity(LoginActivity::class,true)
                authManager.first = true
                if(Kor) {
                    authManager.setLang = "KOR"
                }
                if(Eng) {
                    authManager.setLang = "ENG"
                }
            }
        }

        initCheckRadioBtn()
        initEnableBtn()
    }
    private fun requestServer(lang : String) {
        networkManager.requestLanguage(type = "speech",data = LanguageBody(lang)).safeLoginEnqueue(
            onSuccess = {
                authManager.token = it
                resources.updateConfiguration(configuration,resources.displayMetrics)
                val intent = baseContext.packageManager.getLaunchIntentForPackage(
                    baseContext.packageName
                )
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intent)

            },
            onFailure = {
                if(it.code() == 403){
                    toast("재로그인을 해주세요")
                } else {
                    toast("언어 수정에 실패했습니다")
                }
            },
            onError = {
                networkErrorToast()
            }
        )
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