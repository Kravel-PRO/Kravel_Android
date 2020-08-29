package com.kravelteam.kravel_android.ui.signup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_finish_sign_up.*

class FinishSignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_sign_up)


        setResult(Activity.RESULT_OK)

        btn_signup_finish.setOnClickListener {
               Intent(GlobalApp,MainActivity::class.java).run {
                   startActivityForResult(this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                       REQUEST_ACTIVITY_MAIN) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_ACTIVITY_MAIN) {
            finish()
        }
    }

    companion object {
        const val REQUEST_ACTIVITY_MAIN = 1002
    }
}
