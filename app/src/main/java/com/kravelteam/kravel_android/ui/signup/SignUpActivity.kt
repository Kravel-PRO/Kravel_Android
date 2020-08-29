package com.kravelteam.kravel_android.ui.signup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.data.request.SignUpRequest
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toJson
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {

    var checkemail = false
    var checkpw = false
    var checkpwck = false
    var checknickname = false
    var checkBtn = false

    var email: String =""
    var pw : String = ""
    var gender : String =""
    var nickname : String =""

    private val networkManager : NetworkManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        img_signup_back.setOnClickListener {
            finish()
        }

        edit_signup_email.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =

                if(s.toString()=="")
                {
                    edit_signup_email.setBackgroundResource(R.drawable.signup_edit_style)
                    tv_signup_email_warning.visibility = View.GONE
                    checkemail = false
                    checkedBtn()
                }
                else {
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {


                        edit_signup_email.setBackgroundResource(R.drawable.signup_edit_style_warning)
                        edit_signup_email.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditTextWarning
                            )
                        )
                        tv_signup_email_warning.visibility = View.VISIBLE

                        checkemail = false
                        checkedBtn()
                    }
                    else
                    {
                        edit_signup_email.setBackgroundResource(R.drawable.signup_edit_style_true)
                        edit_signup_email.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditText
                            )
                        )
                        tv_signup_email_warning.visibility = View.GONE
                        email = edit_signup_email.text.toString()
                        checkemail = true
                        checkedBtn()

                    }
                }


        })

        edit_signup_pw.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length <6|| s.isNullOrEmpty() )
                {
                    if(s.toString() == "")
                    {
                        edit_signup_pw.setBackgroundResource(R.drawable.signup_edit_style)
                        edit_signup_pw.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorDarkGrey))
                        tv_signup_pw_warning.visibility = View.GONE
                    }
                    else {
                        edit_signup_pw.setBackgroundResource(R.drawable.signup_edit_style_warning)
                        edit_signup_pw.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditTextWarning
                            )
                        )

                        tv_signup_pw_warning.visibility = View.VISIBLE
                    }
                    checkpw = false
                    checkedBtn()
                }
                else{
                    edit_signup_pw.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_pw.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    tv_signup_pw_warning.visibility = View.GONE
                    pw = edit_signup_pw.text.toString()
                    checkpw = true
                    checkedBtn()
                }

                if(s.toString().equals(edit_signup_pwck.text.toString()))
                {
                    edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_pwck.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    tv_signup_pwck_warning.visibility = View.GONE
                    checkpwck = true
                    checkedBtn()
                }
                else
                {
                    if(!edit_signup_pwck.text.toString().isNullOrBlank()) {
                        edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style_warning)
                        edit_signup_pwck.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditTextWarning
                            )
                        )
                        tv_signup_pwck_warning.visibility = View.VISIBLE

                        checkpwck = false
                        checkedBtn()
                    }
                }
            }

        })

        edit_signup_pwck.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!(edit_signup_pw.text.toString().equals(edit_signup_pwck.text.toString()))|| s.isNullOrEmpty() )
                {
                    if(s.toString() == "")
                    {
                        edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style)
                        edit_signup_pwck.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorDarkGrey))
                        tv_signup_pwck_warning.visibility = View.GONE


                    }
                    else {
                        edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style_warning)
                        edit_signup_pwck.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditTextWarning
                            )
                        )
                        tv_signup_pwck_warning.visibility = View.VISIBLE
                    }
                    checkpwck = false
                    checkedBtn()
                }
                else
                {
                    edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_pwck.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    tv_signup_pwck_warning.visibility = View.GONE
                    checkpwck = true
                    checkedBtn()
                }
            }

        })

        edit_signup_nickname.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.toString()=="")
                {
                    edit_signup_nickname.setBackgroundResource(R.drawable.signup_edit_style)
                    checknickname = false
                    checkedBtn()

                }
                else {
                    if (s!!.length > 7) {

                        edit_signup_nickname.setBackgroundResource(R.drawable.signup_edit_style_warning)
                        edit_signup_nickname.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditTextWarning
                            )
                        )
                        checknickname = false
                        checkedBtn()

                    } else {
                        edit_signup_nickname.setBackgroundResource(R.drawable.signup_edit_style_true)
                        edit_signup_nickname.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.colorSignUpEditText
                            )
                        )
                        nickname = edit_signup_nickname.text.toString()
                        checknickname = true
                        checkedBtn()
                    }
                }
            }

        })

        radio_signup_gender.setOnCheckedChangeListener(
            object : RadioGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                    if(checkedId == R.id.radio_signup_man)
                    {
                        gender ="MAN"
                        radio_signup_man.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorPoint))
                        radio_signup_woman.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorGender))
                        checkedBtn()
                    }
                    else if(checkedId == R.id.radio_signup_woman)
                    {
                        gender="WOMAN"
                        radio_signup_woman.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorPoint))
                        radio_signup_man.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorGender))
                        checkedBtn()
                    }
                }

            }
        )


        btn_signup.setOnClickListener {
            checkedBtn()
            if(checkBtn) {
                requestSignUp()
                Timber.e("id :::: " + email)
                Timber.e("pw :::: " + pw)
                Timber.e("nickname :::" + nickname)
                Timber.e("gender ::: "+ gender)

            }
        }

    }
    fun checkedBtn()
    {
        if(checkemail && checkpw && checkpwck && checknickname && !gender.equals("")) {

            btn_signup.setBackgroundResource(R.drawable.signup_btn_style_true)
            btn_signup.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
            checkBtn = true
        }
        else
        {
            btn_signup.setBackgroundResource(R.drawable.signup_btn_style)
            btn_signup.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGender))
            checkBtn = false
        }
    }
    private fun requestSignUp(){
        val data = SignUpRequest(email,pw,nickname,gender,"KOR")
        networkManager.requestSignUp(data).safeEnqueue(
            onSuccess = {
                toast("성공")
                Intent(GlobalApp,FinishSignUpActivity::class.java).run {
                    startActivityForResult(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        REQUEST_FINISH_SIGN_UP) }
            },
            onFailure = {
                when(it.errorBody().toJson().error?.code){
                    400 -> {
                        toast("이미 존재하는 이메일입니다")
                    }
                }
            },
            onError = {
                toast("에러")
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_FINISH_SIGN_UP) {
            finish()
        }
    }

    companion object {
        private const val REQUEST_FINISH_SIGN_UP = 1000
    }
}
