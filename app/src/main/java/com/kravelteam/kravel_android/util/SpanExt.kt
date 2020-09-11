package com.kravelteam.kravel_android.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

fun String.setTextColorSubString(substring: String, color: Int): SpannableString {
    if (!this.contains(substring)) return SpannableString(this)

    val targetStartIndex = this.indexOf(substring)
    val targetEndIndex = targetStartIndex + substring.length

    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            targetStartIndex,
            targetEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}