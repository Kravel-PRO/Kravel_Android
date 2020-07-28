package com.hyesun.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.setOnDebounceClickListener
import com.hyesun.kravel_android.data.common.SearchWord
import com.hyesun.kravel_android.ui.adapter.SearchViewPagerAdapter
import com.hyesun.kravel_android.ui.adapter.SearchWordRecyclerview
import com.hyesun.kravel_android.util.setGone
import com.hyesun.kravel_android.util.setVisible
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private val wordAdapter : SearchWordRecyclerview by lazy { SearchWordRecyclerview() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initSearchView()
        initRecycler()
    }

    private fun initViewPager() {
        vp_search_select.adapter = SearchViewPagerAdapter(childFragmentManager,2, arrayListOf("연예인 별","드라마/영화 별"))
        vp_search_select.offscreenPageLimit = 2

//        tl_search_select.getTabAt(0)?.text = "연예인 별"
//        tl_search_select.getTabAt(1)?.text = "드라마/영화 별"

        vp_search_select.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl_search_select))
        tl_search_select.setupWithViewPager(vp_search_select)

        tl_search_select.addOnTabSelectedListener( object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?){}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.select() }
        })
    }

    private fun initRecycler(){
        rv_search_recent.apply {
            adapter = wordAdapter
        }
        wordAdapter.initData(
           listOf(
               SearchWord("아이유"),
               SearchWord("호텔델루나")
           )
        )
    }

    private fun initSearchView(){
        edt_search_word.setOnClickListener {
            cl_search_tab.setGone()
            cl_search_recent.setVisible()
        }
    }
}
