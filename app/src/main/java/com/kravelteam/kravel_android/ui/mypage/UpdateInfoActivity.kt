package com.kravelteam.kravel_android.ui.mypage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.request.UpdateInfo
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.onTextChangeListener
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_update_info.*
import org.koin.android.ext.android.inject

@Suppress("DEPRECATION")
class UpdateInfoActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)


        edt_update_info_nickname.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("nickname"))
        val gender = intent.getStringExtra("gender")
        if(gender == "MAN"){
            rb_update_info_man.isChecked = true
            rb_update_info_man.setTextColor(resources.getColor(R.color.colorCoral))
        } else {
            rb_update_info_woman.isChecked = true
            rb_update_info_woman.setTextColor(resources.getColor(R.color.colorCoral))
        }

        initCheckEditText()
        initCheckRadioBtn()
        initChangeEditText()
        initEnableBtn()
        initUpdateUserInfo()
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
            val gender = if(rb_update_info_man.isChecked){
                "MAN"
            } else "WOMAN"

            val data = UpdateInfo(
                "",
                "",
                gender,
                edt_update_info_nickname.text.toString()
            )
            networkManager.requestUpdateInfo("nickNameAndGender",data).safeEnqueue(
                onSuccess = {
                    toast("정보 수정이 완료되었습니다")
                    finish()
                },
                onFailure = {
                    toast("업데이트를 실패했습니다")
                },
                onError = {
                    networkErrorToast()
                }
            )
        }
    }
}