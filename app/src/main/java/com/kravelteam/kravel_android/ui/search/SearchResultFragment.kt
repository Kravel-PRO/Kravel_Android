package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.data.common.SearchResult
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.SearchResultRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.activity_search_content.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_search_result.lottie_detail_loading
import kotlinx.android.synthetic.main.fragment_search_result.root
import org.koin.android.ext.android.inject
import timber.log.Timber

class SearchResultFragment : Fragment() {

    private val networkManager : NetworkManager by inject()
    private val authManager: AuthManager by inject()
    private val searchResultAdapter : SearchResultRecyclerview by lazy { SearchResultRecyclerview() }
    private lateinit var lottie : LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lottie = lottie_detail_loading
        initSearchResult()
    }

    private fun onLoading(){
        lottie.apply {
            setAnimation("heart_loading.json")
            playAnimation()
            loop(true)
        }
        root.setGone()
        lottie_detail_loading.setVisible()
    }

    private fun offLoading(){
        root.setVisible()
        lottie_detail_loading.setGone()
    }

    private fun initSearchResult(){
        onLoading()
        if (newToken(authManager,networkManager)) {
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
                        img_no_search_result_icon.setVisible()
                        txt_search_recent_empty.setVisible()
                    } else {
                        var searchData = mutableListOf<SearchResult>()
                        if (!it.data.result.celebrities.isNullOrEmpty()) {
                            it.data.result.celebrities.forEach {
                                searchData.add(SearchResult(it.celebrityId,it.celebrityName,it.imageUrl!!,"celeb"))
                            }
                        }
                        if (!it.data.result.medias.isNullOrEmpty()) {
                            it.data.result.medias.forEach {
                                searchData.add(SearchResult(it.mediaId, it.title, it.imageUrl!!,"media"))
                            }
                        }
                        txt_search_recent_empty.setGone()
                        img_no_search_result_icon.setGone()
                        rv_search_result.setVisible()

                        searchResultAdapter.initData(searchData)
                        offLoading()
                    }
                },
                onFailure = {
                    when {
                        it.code() == 400 -> {
                            toast(resources.getString(R.string.errorSearch))
                        }
                        it.code() == 403 -> {
                            toast(resources.getString(R.string.errorReLogin))
                        }
                        else -> {
                            toast(resources.getString(R.string.errorClient))
                        }
                    }
                    offLoading()
                },
                onError = {
                    networkErrorToast()
                    offLoading()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
            offLoading()
        }
    }
}