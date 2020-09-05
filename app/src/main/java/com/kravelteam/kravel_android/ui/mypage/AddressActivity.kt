package com.kravelteam.kravel_android.ui.mypage

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.network.NetworkService
import com.kravelteam.kravel_android.ui.adapter.AddressRecyclerview
import com.kravelteam.kravel_android.util.safeEnqueue
import kotlinx.android.synthetic.main.activity_address.*
import org.koin.android.ext.android.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class AddressActivity : AppCompatActivity() {

    private val networkManager : NetworkManager by inject()
    private lateinit var addressAdapter: AddressRecyclerview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        addressAdapter = AddressRecyclerview (
            onFinish = {
                val intent = intent
                intent.putExtra("address", it)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        )
        rv_address.adapter = addressAdapter

        initSearch()

        img_address_back.setOnDebounceClickListener { finish() }
    }

    private fun initSearch(){
        edt_address_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                initRecycler()
            }
            true
        }

        img_report_search_icon.setOnDebounceClickListener {
            initRecycler()
        }
    }

    private fun initRecycler(){
        val query = edt_address_search.text.toString()
        val token = "KakaoAK " + resources.getString(R.string.kakao_api_key)

        Timber.e("검색22")

        networkManager.requestSearchAddress(token, query).safeEnqueue(
            onSuccess = {
                Timber.e("검색")
                addressAdapter.initData(it.documents)
            },
            onFailure = {
                Timber.e("실패")
            },
            onError = {
                Timber.e("$it")
            }
        )
    }
}