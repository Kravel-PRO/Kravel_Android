package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.ui.adapter.SearchViewPagerAdapter
import com.kravelteam.kravel_android.ui.map.fragmentBackPressed
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), fragmentBackPressed {

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
        initSearch()
    }

    private fun initViewPager() {

        vp_search_select.adapter = SearchViewPagerAdapter(childFragmentManager,2, arrayListOf(resources.getString(R.string.celebPart),resources.getString(R.string.mediaPart)))
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

    override fun onBackPressed() : Boolean {
        return true
    }
}
