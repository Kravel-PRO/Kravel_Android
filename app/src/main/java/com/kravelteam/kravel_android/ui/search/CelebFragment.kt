package com.kravelteam.kravel_android.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.HorizontalItemDecorator
import com.kravelteam.kravel_android.common.VerticalItemDecorator
import com.kravelteam.kravel_android.common.newToken
import com.kravelteam.kravel_android.data.response.CelebResponse
import com.kravelteam.kravel_android.network.AuthManager
import com.kravelteam.kravel_android.network.NetworkManager
import com.kravelteam.kravel_android.ui.adapter.CelebRecyclerview
import com.kravelteam.kravel_android.ui.main.MainActivity
import com.kravelteam.kravel_android.util.*
import kotlinx.android.synthetic.main.fragment_celeb.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class CelebFragment() : Fragment() {

    private val networkManager : NetworkManager by inject()
    private val authManager: AuthManager by inject()
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
        if (newToken(authManager,networkManager)){
            networkManager.requestCelebList(0,100).safeEnqueue(
                onSuccess = {
                    celebAdapter = CelebRecyclerview()

                    val anim = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_anim_fall)
                    rv_celeb_list.apply {
                        adapter = celebAdapter
                        addItemDecoration(HorizontalItemDecorator(24))
                        addItemDecoration(VerticalItemDecorator(38))
                        layoutAnimation = anim
                    }
                    if(!it.data.result.content.isNullOrEmpty()) celebAdapter.initData(it.data.result.content)
                },
                onFailure = {
                    if(it.code() == 403) {
                        toast(resources.getString(R.string.errorReLogin))
                    } else {
                        toast(resources.getString(R.string.errorClient))
                    }
                },
                onError = {
                    networkErrorToast()
                }
            )
        } else {
            toast(resources.getString(R.string.errorNetwork))
        }

    }
}