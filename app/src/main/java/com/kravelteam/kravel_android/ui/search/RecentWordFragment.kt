package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.ui.adapter.SearchWordRecyclerview
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible
import kotlinx.android.synthetic.main.fragment_recent_word.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RecentWordFragment : Fragment() {

    private lateinit var wordAdapter : SearchWordRecyclerview

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_word, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler() {
        wordAdapter = SearchWordRecyclerview(
            onAllDelete = {
                activity?.runOnUiThread {
                    cl_search_recent_word_empty.visibility = View.VISIBLE
                    cl_search_recent_word_list.visibility = View.GONE
                }
            },
            onSearch = {
                (activity as SearchContentActivity).addRecentWord(it)
            }
        )
        rv_search_recent.apply {
            adapter = wordAdapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val data = KravelApplication.db.searchWordDao().getAll()
            val order = data.reversed().toMutableList()
            Timber.e("${data.size}")
            if (data.size > 0) {
                activity?.runOnUiThread {
                    cl_search_recent_word_empty.visibility = View.GONE
                    cl_search_recent_word_list.visibility = View.VISIBLE
                }
            } else {
                activity?.runOnUiThread {
                    cl_search_recent_word_empty.visibility = View.VISIBLE
                    cl_search_recent_word_list.visibility = View.GONE
                }
            }

            wordAdapter.initData(order)
        }
    }
}