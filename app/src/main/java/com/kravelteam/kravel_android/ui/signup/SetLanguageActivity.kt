package com.kravelteam.kravel_android.ui.signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.LanguageBody
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.login.LoginActivity
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_set_language.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

@Suppress("DEPRECATION")
class SetLanguageActivity : AppCompatActivity() {
    private val authManager : AuthManager by inject()
    private var checkLang = false
    private var strLang : String? = null
    private val networkManager : NetworkManager by inject()
    private lateinit var configuration: Configuration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

        intent.getStringExtra("my")?.let {
            img_set_lang_back.setOnDebounceClickListener {
                finish()
            }
            cl_set_language_toolbar.setVisible()
            if(authManager.setLang == "KOR"){
                strLang = "KOR"
                rb_set_language_korea.isChecked = true
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorCoral))
                rb_set_language_english.setTextColor(resources.getColor(R.color.colorDarkGrey))
                checkLang=true
            } else {
                strLang ="ENG"
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
                if(strLang == "KOR") {
                    val ko = Locale.KOREA
                    configuration.locale = ko
                    requestServer("KOR")
                    authManager.setLang = "KOR"
                } else {
                    val en = Locale.US
                    configuration.locale = en
                    requestServer("ENG")
                    authManager.setLang = "ENG"
                }
            } else {
                authManager.first = true
                if(strLang == "KOR") {
                    authManager.setLang = "KOR"
                    val ko = Locale.KOREA
                    configuration.locale = ko
                } else {
                    authManager.setLang = "ENG"
                    val en = Locale.US
                    configuration.locale = en

                }
                resources.updateConfiguration(configuration,resources.displayMetrics)
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
    private fun requestServer(lang : String) {
        if(newToken(authManager,networkManager)) {
            networkManager.requestLanguage(type = "speech", data = LanguageBody(lang))
                .safeLoginEnqueue(
                    onSuccess = {
                        authManager.token = it
                        resources.updateConfiguration(configuration, resources.displayMetrics)
                        val intent = baseContext.packageManager.getLaunchIntentForPackage(
                            baseContext.packageName
                        )
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        finish()
                        startActivity(intent)

                    },
                    onFailure = {
                        if (it.code() == 403) {
                            toast(resources.getString(R.string.errorReLogin))
                        } else {
                            toast(resources.getString(R.string.errorClient))
                        }

                    },
                    onError = {
                        networkErrorToast()
                    }
                )
        }else{
            toast(resources.getString(R.string.errorNetwork))
        }
    }

    private fun initCheckRadioBtn(){
        rb_set_language_korea.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                strLang = "KOR"
                initEnableBtn()
            } else {
                rb_set_language_korea.setTextColor(resources.getColor(R.color.colorDarkGrey))
            }
        }

        rb_set_language_english.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {

                rb_set_language_english.setTextColor(resources.getColor(R.color.colorCoral))
                checkLang = true
                strLang ="ENG"
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