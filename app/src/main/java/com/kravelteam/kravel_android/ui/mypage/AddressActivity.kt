package com.kravelteam.kravel_android.ui.mypage

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.AddressRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_address.*
import org.koin.android.ext.android.inject

class AddressActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private lateinit var addressAdapter: AddressRecyclerview
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)


        img_address_back.setOnDebounceClickListener { finish() }
        cl_address_sample_space.setVisible()
        cl_address_result_space.setGone()
        edt_address_search

        initSearch()
        initChangeEdit()
    }

    private fun initChangeEdit(){
        edt_address_search.onTextChangeListener(
            onTextChanged = {
                if(!edt_address_search.text.isNullOrBlank()){
                    edt_address_search.setBackgroundResource(R.drawable.signup_edit_style_true)
                } else {
                    edt_address_search.setBackgroundResource(R.drawable.signup_edit_style)
                }
            }
        )
    }

    private fun initSearch(){
        edt_address_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                initSearchAddress()
            }
            true
        }

        img_report_search_icon.setOnDebounceClickListener {
            initSearchAddress()
        }
    }
    private fun initSearchAddress(){
        cl_address_sample_space.setGone()
        cl_address_result_space.setVisible()
        query = edt_address_search.text.toString()
        val token = resources.getString(R.string.kakao_api_key)

        addressAdapter = AddressRecyclerview (
            query,
            onFinish = {
                val intent = intent
                intent.putExtra("address", it)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        )
        rv_address.adapter = addressAdapter

        networkManager.requestSearchAddress(token, query).safeEnqueue(
            onSuccess = {
                if(it.documents.isNullOrEmpty()){
                    rv_address.setGone()
                    img_no_empty_result.setVisible()
                    txt_address_empty.setVisible()
                } else {
                    rv_address.setVisible()
                    img_no_empty_result.setGone()
                    txt_address_empty.setGone()
                    addressAdapter.initData(it.documents)
                }
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                toast("에러")
            }
        )
    }
}