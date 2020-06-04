package com.hyesun.kravel_android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.R.color.colorSignUpEditTextWarning
import com.hyesun.kravel_android.R.drawable.signup_btn_style_true
import kotlinx.android.synthetic.main.activity_sign_up.*

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edit_signup_email.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches() || s?.length ==0  )
                {
                    edit_signup_email.setBackgroundResource(R.drawable.signup_edit_style_warning)
                    edit_signup_email.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditTextWarning))
                    tv_signup_email_warning.visibility = View.VISIBLE
                    checkemail = false
                    checkedBtn()
                }
                else
                {
                    edit_signup_email.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_email.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    tv_signup_email_warning.visibility = View.GONE
                    email = edit_signup_email.toString()
                    checkemail = true
                    checkedBtn()

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
                    edit_signup_pw.setBackgroundResource(R.drawable.signup_edit_style_warning)
                    edit_signup_pw.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditTextWarning))
                    tv_signup_pw_warning.visibility = View.VISIBLE
                    checkpw = false
                    checkedBtn()
                }
                else{
                    edit_signup_pw.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_pw.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    tv_signup_pw_warning.visibility = View.GONE
                    pw = edit_signup_pw.toString()
                    checkpw = true
                    checkedBtn()
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
                    edit_signup_pwck.setBackgroundResource(R.drawable.signup_edit_style_warning)
                    edit_signup_pwck.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditTextWarning))
                    tv_signup_pwck_warning.visibility = View.VISIBLE
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
                if(s!!.length>7 || s.isEmpty())
                {
                    edit_signup_nickname.setBackgroundResource(R.drawable.signup_edit_style_warning)
                    edit_signup_nickname.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditTextWarning))
                    checknickname = false
                    checkedBtn()
                }
                else
                {
                    edit_signup_nickname.setBackgroundResource(R.drawable.signup_edit_style_true)
                    edit_signup_nickname.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorSignUpEditText))
                    nickname = edit_signup_nickname.toString()
                    checknickname = true
                    checkedBtn()
                }
            }

        })

        radio_signup_gender.setOnCheckedChangeListener(
            object : RadioGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                    if(checkedId == R.id.radio_signup_man)
                    {
                        gender ="man"
                        radio_signup_man.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorPoint))
                        radio_signup_woman.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorGender))
                        checkedBtn()
                    }
                    else if(checkedId == R.id.radio_signup_woman)
                    {
                        gender="woman"
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
                val intent = Intent(applicationContext, FinishSignUpActivity::class.java)
                startActivity(intent)
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
            btn_signup.setBackgroundResource(R.drawable.signup_edit_style)
            btn_signup.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorGender))
            checkBtn = false
        }
    }
}
