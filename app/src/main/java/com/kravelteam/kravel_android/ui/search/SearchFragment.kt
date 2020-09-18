package com.kravelteam.kravel_android.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.common.SearchWord
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.data.response.MediaResponse
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.adapter.SearchViewPagerAdapter
import com.kravelteam.kravel_android.ui.adapter.SearchWordRecyclerview
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var lottie : LottieAnimationView
    private val networkManager : NetworkManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lottie = lottie_loading

        initViewPager()
        initSearch()
    }

    private fun onLoading(){
        cl_search_main.setGone()
        lottie_loading.setVisible()
        lottie.apply {
            setAnimation("loading_map.json")
            playAnimation()
            loop(true)
        }
    }
    private fun offLoading(){
        lottie_loading.setGone()
        cl_search_main.setVisible()
    }

    private fun initViewPager() {
        onLoading()

        var celeb : List<CelebResponse> = emptyList()
        var media : List<MediaResponse> = emptyList()
        networkManager.requestCelebList().safeEnqueue(
            onSuccess = {
                if(!it.data.result.content.isNullOrEmpty()) celeb = it.data.result.content
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )

        networkManager.requestMediaList().safeEnqueue(
            onSuccess = {
                if(!it.data.result.content.isNullOrEmpty()) media = it.data.result.content
            },
            onFailure = {
                toast("실패")
            },
            onError = {
                networkErrorToast()
            }
        )
        Handler().postDelayed({
            offLoading()
        },200)

        vp_search_select.adapter = SearchViewPagerAdapter(childFragmentManager,2, arrayListOf(resources.getString(R.string.celebPart),resources.getString(R.string.mediaPart)),celeb,media)
        vp_search_select.offscreenPageLimit = 2

        vp_search_select.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl_search_select))
        tl_search_select.setupWithViewPager(vp_search_select)

        tl_search_select.addOnTabSelectedListener( object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?){}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.select() }
        })
    }

    private fun initSearch(){
        edt_search_word_area.setOnDebounceClickListener {
            startActivity(SearchContentActivity::class)
        }
    }
}
