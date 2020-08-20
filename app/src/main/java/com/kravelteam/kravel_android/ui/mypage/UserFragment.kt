package com.kravelteam.kravel_android.ui.mypage

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.ui.signup.SetLanguageActivity
import com.kravelteam.kravel_android.util.startActivity
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {

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
    }

    private fun initSetting(){
        cl_user_update_info.setOnDebounceClickListener {
            startActivity(UpdateInfoActivity::class)
        }

        cl_user_update_pw.setOnDebounceClickListener {
            startActivity(UpdatePwActivity::class)
        }

        cl_user_set_lang.setOnDebounceClickListener {
            startActivity(SetLanguageActivity::class)
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

        view.btn_logout_cancel.setOnDebounceClickListener {
            dialog.cancel()
        }

        dialog.apply {
            setView(view)
            setCancelable(false)
            show()
        }
    }
}
