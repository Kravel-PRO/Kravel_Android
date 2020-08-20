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
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import kotlinx.android.synthetic.main.fragment_drama.*

class DramaFragment : Fragment() {

    private val celebAdapter : CelebRecyclerview by lazy { CelebRecyclerview(true) }

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
        rv_drama_list.apply {
            adapter = celebAdapter
            addItemDecoration(HorizontalItemDecorator(24))
            addItemDecoration(VerticalItemDecorator(36))
        }
        celebAdapter.initData(
            listOf(
                CelebResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg","사이코지만 괜찮아",2020),
                CelebResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg","사이코지만 괜찮아",2020),
                CelebResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg","사이코지만 괜찮아",2020),
                CelebResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg","사이코지만 괜찮아",2020),
                CelebResponse("https://image.chosun.com/sitedata/image/202006/09/2020060902224_0.jpg","사이코지만 괜찮아",2020)
            )
        )
    }
}