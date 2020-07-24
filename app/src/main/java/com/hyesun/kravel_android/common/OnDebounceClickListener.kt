package com.hyesun.kravel_android.common

import android.view.View

class OnDebounceClickListener(private val listener: (View) -> Unit) : View.OnClickListener {
    override fun onClick(v: View?) {
        val now = System.currentTimeMillis()
        if (now < lastTime + INTERVAL) return
        lastTime = now
        v?.run(listener)
    }

    companion object {
        private const val INTERVAL: Long = 300L
        private var lastTime: Long = 0
    }
}


infix fun View.setOnDebounceClickListener(listener: (View) -> Unit) {
    this.setOnClickListener(OnDebounceClickListener {
        it.run(listener)
    })
}