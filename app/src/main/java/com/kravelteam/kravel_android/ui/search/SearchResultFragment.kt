package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_search_content.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber

class SearchResultFragment : Fragment() {

    private val networkManager : NetworkManager by inject()
    private val searchResultAdapter : CelebRecyclerview by lazy { CelebRecyclerview() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchResult()
    }

    private fun initSearchResult(){
        networkManager.requestSearchResult(activity?.edt_search_word?.text.toString()).safeEnqueue(
            onSuccess = {
                //리사이클러 초기화
                rv_search_result.apply {
                    adapter = searchResultAdapter
                    addItemDecoration(HorizontalItemDecorator(24))
                    addItemDecoration(VerticalItemDecorator(36))
                }

                //데이터가 빈 것이 있는지 확인
                if (it.data.result.celebrities.isNullOrEmpty() and it.data.result.medias.isNullOrEmpty()) {
                    txt_search_recent_empty.setVisible()
                } else {
                    var searchData = mutableListOf<CelebResponse>()
                    if (!it.data.result.celebrities.isNullOrEmpty()) {
                        it.data.result.celebrities.forEach {
                            searchData.add(it)
                        }
                    }
                    if (!it.data.result.medias.isNullOrEmpty()) {
                        it.data.result.medias.forEach {
                            searchData.add(CelebResponse(it.mediaId, it.title, it.imageUrl))
                        }
                    }
                    txt_search_recent_empty.setGone()
                    rv_search_result.setVisible()

                    searchResultAdapter.initData(searchData)
                }
            },
            onFailure = {
                toast("검색어 입력을 확인해주세요")
            },
            onError = {
                toast("검색어 입력을 확인해주세요")
            }
        )
    }
}