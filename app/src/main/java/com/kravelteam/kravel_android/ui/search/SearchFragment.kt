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
import com.google.android.material.tabs.TabLayout
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.common.SearchWord
import com.kravelteam.kravel_android.data.response.CelebResponse
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
//        initSearchView()
//        initRecycler()
//
//        addSearchWord()
    }

    private fun initViewPager() {
        vp_search_select.adapter = SearchViewPagerAdapter(childFragmentManager,2, arrayListOf("연예인 별","드라마/영화 별"))
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

//    private fun initVisibleRecentWord(){
//        if(dataSize == 0){
//            //엔터 누를시 최근 검색어 내역 보이기
//            cl_search_recent_word.setGone()
//            rv_search_recent.setGone()
//
//            //엔터 누를시 최근 검색어가 없습니다 보이기
//            cl_search_recent_word_empty.setVisible()
//        } else {
//            //엔터 누를시 최근 검색어 내역 보이기
//            cl_search_recent_word.setVisible()
//            rv_search_recent.setVisible()
//
//            //엔터 누를시 최근 검색어가 없습니다 지우기
//            cl_search_recent_word_empty.setGone()
//        }
//    }


//    @SuppressLint("ClickableViewAccessibility")
//    private fun initSearchView(){
//        edt_search_word.setOnTouchListener { _, _ ->
//            showKeyboard()
//            edt_search_word.requestFocus()
//            edt_search_word.imeOptions = EditorInfo.IME_ACTION_SEARCH
//            cl_search_tab.setGone()
//            cl_search_recent.setVisible()
//            img_search_back.setVisible()
//            initVisibleRecentWord()
//            true
//        }
//
//        img_search_back.setOnDebounceClickListener {
//            hideKeyboard()
//            cl_search_tab.setVisible()
//            cl_search_recent.setGone()
//            img_search_back.setGone()
//
//            txt_search_recent_title.text = "최근 검색어"
//            view_search_line.setGone()
//            rv_search_recent_result.setGone()
//
//            edt_search_word.text = Editable.Factory.getInstance().newEditable("")
//        }
//    }

//    }
}
