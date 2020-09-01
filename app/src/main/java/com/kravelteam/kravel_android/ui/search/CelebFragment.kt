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
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.util.safeEnqueue
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.fragment_celeb.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class CelebFragment : Fragment() {

    private val networkManager : NetworkManager by inject()
    private lateinit var celebAdapter : CelebRecyclerview

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_celeb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler(){


        celebAdapter = CelebRecyclerview()

        rv_celeb_list.apply {
            adapter = celebAdapter
            addItemDecoration(HorizontalItemDecorator(24))
            addItemDecoration(VerticalItemDecorator(36))
        }

        networkManager.requestCelebList().safeEnqueue(
            onSuccess = {
                Timber.e(it.data.result[0].celebrityName)
                celebAdapter.initData(it.data.result)

            },
            onFailure = {
                toast("실패")
            },
            onError = {
                Timber.e("$it")
            }
        )
//        celebAdapter.initData(
//            listOf(
//                CelebResponse("https://www.instagram.com/p/B20TsJegiq4/media/?size=l","지창욱",null),
//                CelebResponse("https://www.instagram.com/p/B20TsJegiq4/media/?size=l","지창욱",null),
//                CelebResponse("https://www.instagram.com/p/B20TsJegiq4/media/?size=l","지창욱",null),
//                CelebResponse("https://www.instagram.com/p/B20TsJegiq4/media/?size=l","지창욱",null),
//                CelebResponse("https://www.instagram.com/p/B20TsJegiq4/media/?size=l","지창욱",null)
//            )
//        )
    }
}