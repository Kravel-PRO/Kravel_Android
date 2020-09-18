package com.kravelteam.kravel_android.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.util.fadeInWithVisible
import com.kravelteam.kravel_android.util.setGone
import com.kravelteam.kravel_android.util.setVisible

open class BaseFragment(private val contentLayoutId : Int) : Fragment() {

    private lateinit var loadingView : LottieAnimationView
    private lateinit var root : View
    @get:JvmName("getContext_") private lateinit var view : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(contentLayoutId, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    fun loadingOn() {
        root = view.findViewById(R.id.root)

        loadingView = view.findViewById(R.id.loading)!!

        loadingView.apply {
            setAnimation("loading_map.json")
            playAnimation()
            loop(true)
        }

        loadingView.setVisible()
        root.setGone()
    }

    fun loadingOff() {
        loadingView.setGone()
        root.setVisible()
    }

    fun loadingOffWithFadeIn() {
        loadingView.setGone()
        root.fadeInWithVisible(500)
    }

    fun loadingOffWithOutFadeIn() {
        loadingView.setGone()
        root.setVisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
