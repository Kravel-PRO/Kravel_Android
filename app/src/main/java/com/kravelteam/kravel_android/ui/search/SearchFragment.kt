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

    private val networkManager : NetworkManager by inject()
    private lateinit var wordAdapter : SearchWordRecyclerview
    private val searchResultAdapter : CelebRecyclerview by lazy { CelebRecyclerview() }

    private var dataSize = 0

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

        addSearchWord()
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

    private fun initVisibleRecentWord(){
        if(dataSize == 0){
            //엔터 누를시 최근 검색어 내역 보이기
            cl_search_recent_word.setGone()
            rv_search_recent.setGone()

            //엔터 누를시 최근 검색어가 없습니다 보이기
            cl_search_recent_word_empty.setVisible()
        } else {
            //엔터 누를시 최근 검색어 내역 보이기
            cl_search_recent_word.setVisible()
            rv_search_recent.setVisible()

            //엔터 누를시 최근 검색어가 없습니다 지우기
            cl_search_recent_word_empty.setGone()
        }
    }

    private fun initRecycler(){
        wordAdapter = SearchWordRecyclerview(
            onUpdateSize = {
                dataSize--
            }
        )
        rv_search_recent.apply {
            adapter = wordAdapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = KravelApplication.db.searchWordDao().getAll()
                wordAdapter.initData(data)
                dataSize = data.size
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchView(){
        edt_search_word.setOnTouchListener { _, _ ->
            showKeyboard()
            edt_search_word.requestFocus()
            edt_search_word.imeOptions = EditorInfo.IME_ACTION_SEARCH
            cl_search_tab.setGone()
            cl_search_recent.setVisible()
            img_search_back.setVisible()
            initVisibleRecentWord()
            true
        }

        img_search_back.setOnDebounceClickListener {
            hideKeyboard()
            cl_search_tab.setVisible()
            cl_search_recent.setGone()
            img_search_back.setGone()

            txt_search_recent_title.text = "최근 검색어"
            view_search_line.setGone()
            rv_search_recent_result.setGone()

            edt_search_word.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    private fun addSearchWord(){
        edt_search_word.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val edt_word = edt_search_word.text.toString()
                val word = SearchWord(word = edt_word)

                hideKeyboard()

                var find: SearchWord? = null
                CoroutineScope(Dispatchers.IO).launch {
                    find = KravelApplication.db.searchWordDao().findWord(edt_word)
                }

                if (find != null) {
                    wordAdapter.deleteData(edt_word)
                    CoroutineScope(Dispatchers.IO).launch {
                        KravelApplication.db.searchWordDao().deleteWord(edt_word)
                    }
                } else {
                    if(dataSize >= 10) { //10개 이상일 때 첫번째 data 삭제
                        wordAdapter.deleteFirstData()
                    } else {
                        dataSize++
                    }
                }
                wordAdapter.addData(word)
                CoroutineScope(Dispatchers.IO).launch {
                    KravelApplication.db.searchWordDao().insertWord(word)
                }

                networkManager.requestSearchResult("펭").safeEnqueue(
                    onSuccess = {
                        rv_search_recent.setGone()
                        txt_search_recent_title.text = "검색 결과"
                        view_search_line.setVisible()
                        //리사이클러 초기화
                        rv_search_recent_result.apply {
                            adapter = searchResultAdapter
                            addItemDecoration(HorizontalItemDecorator(24))
                            addItemDecoration(VerticalItemDecorator(36))
                        }

                        //데이터가 빈 것이 있는지 확인
                        if(it.data.result.celebrities.isNullOrEmpty() and it.data.result.medias.isNullOrEmpty()){
                            txt_search_recent_empty.setVisible()
                        } else {
                            var searchData = mutableListOf<CelebResponse>()
                            if(!it.data.result.celebrities.isNullOrEmpty()) {
                                it.data.result.celebrities.forEach {
                                    searchData.add(it)
                                }
                            }
                            if(!it.data.result.medias.isNullOrEmpty()) {
                                it.data.result.medias.forEach {
                                    searchData.add(CelebResponse(it.mediaId, it.title, it.imageUrl))
                                }
                            }
                            txt_search_recent_empty.setGone()
                            rv_search_recent_result.setVisible()

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
            true
        }

    }
}
