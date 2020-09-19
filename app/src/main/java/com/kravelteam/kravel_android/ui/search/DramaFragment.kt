package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.data.response.MediaResponse
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.adapter.DramaRecyclerview
import com.kravelteam.kravel_android.util.networkErrorToast
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.fragment_drama.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class DramaFragment() : Fragment() {

    private val networkManager : NetworkManager by inject()
    private lateinit var dramaAdapter : DramaRecyclerview

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drama, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler(){
        dramaAdapter = DramaRecyclerview()
        rv_drama_list.apply {
            adapter = dramaAdapter
            addItemDecoration(HorizontalItemDecorator(24))
            addItemDecoration(VerticalItemDecorator(18))
        }


        networkManager.requestMediaList().safeEnqueue(
            onSuccess = {
                if(!it.data.result.content.isNullOrEmpty()) dramaAdapter.initData(it.data.result.content)
            },
            onFailure = {
                if(it.code() == 403) {
                    toast("재로그인을 해주세요!")
                } else {
                    toast("리스트 불러오기에 실패했습니다")
                }
            },
            onError = {
                networkErrorToast()
            }
        )
    }
}