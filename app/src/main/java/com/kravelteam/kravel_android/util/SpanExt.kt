package com.kravelteam.kravel_android.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import androidx.core.content.res.ResourcesCompat
import com.kravelteam.kravel_android.KravelApplication.Companion.GlobalApp

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

fun String.setCustomFontSubString(substring: String, font: Int, sp: Int = 0) : SpannableString {
    if (!this.contains(substring)) return SpannableString(this)

    val targetStartIndex = this.indexOf(substring)
    val targetEndIndex = targetStartIndex + substring.length

    val typeface = Typeface.create(ResourcesCompat.getFont(GlobalApp, font), Typeface.NORMAL)
    val typefaceSpan = CustomTypefaceSpan("", typeface)

    return SpannableString(this).apply {
        setSpan(
            typefaceSpan,
            targetStartIndex,
            targetEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (sp != 0) {
            setSpan(
                AbsoluteSizeSpan(sp.spToPx()),
                targetStartIndex,
                targetEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

class CustomTypefaceSpan(family: String?, private val newType: Typeface) :
    TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(
            ds,
            newType
        )
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(
            paint,
            newType
        )
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old: Typeface = paint.typeface
            oldStyle = old.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }

}