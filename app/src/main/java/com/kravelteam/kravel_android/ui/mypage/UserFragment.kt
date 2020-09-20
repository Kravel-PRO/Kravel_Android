package com.kravelteam.kravel_android.ui.mypage

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.login.LoginActivity
import com.kravelteam.kravel_android.ui.map.fragmentBackPressed
import com.kravelteam.kravel_android.ui.signup.SetLanguageActivity
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.startActivity
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment(), fragmentBackPressed {

    private val authManager : AuthManager by inject()
    private val networkManager : NetworkManager by inject()
    private var nickname: String = ""
    private var gender: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSetting()
        initGetUserInfo()
    }

    private fun initGetUserInfo(){
        if(newToken(authManager,networkManager)) {
            networkManager.requestUserInfo().safeEnqueue(
                onSuccess = {
                    nickname = it.data.result.nickName
                    gender = it.data.result.gender
                    if (authManager.setLang == "KOR") {
                        txt_user_nickname.text = "${nickname}님의 여행을 함께해요!"
                    } else {
                        txt_user_nickname.text = "Let's go on ${nickname}'s trip together!"
                    }

                },
                onFailure = {
                    if (it.code() == 403) {
                        toast("재로그인을 해주세요!")
                    } else {
                        toast("업데이트에 실패했습니다")
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

    private fun initSetting(){
        cl_user_photo.setOnDebounceClickListener {
            Intent(KravelApplication.GlobalApp, AllPhotoReviewActivity::class.java).apply {
                putExtra("review", "my")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { KravelApplication.GlobalApp.startActivity(this) }
        }
        cl_user_scrap.setOnDebounceClickListener {
            startActivity(ScrapActivity::class)
        }
        cl_user_update_info.setOnDebounceClickListener {
            Intent(KravelApplication.GlobalApp, UpdateInfoActivity::class.java).apply {
                putExtra("nickname", nickname)
                putExtra("gender",gender)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { KravelApplication.GlobalApp.startActivity(this) }
        }

        cl_user_update_pw.setOnDebounceClickListener {
            startActivity(UpdatePwActivity::class)
        }

        cl_user_set_lang.setOnDebounceClickListener {
            Intent(KravelApplication.GlobalApp, SetLanguageActivity::class.java).apply {
                putExtra("my", "my")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { KravelApplication.GlobalApp.startActivity(this) }
        }

        cl_user_report.setOnDebounceClickListener {
            startActivity(ReportActivity::class)
        }

        cl_user_logout.setOnDebounceClickListener {
            initDialog()
        }
    }

    private fun initDialog(){
        val dialog = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.btn_logout_cancel.setOnDebounceClickListener {
            dialog.cancel()
        }

        view.btn_logout.setOnDebounceClickListener {
            authManager.autoLogin = false
            startActivity(LoginActivity::class,true)
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        initGetUserInfo()
    }

    override fun onBackPressed() : Boolean {
        return true
    }

}
