package com.kravelteam.kravel_android.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Int.pxToDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.dpToPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.pxToSp() : Int = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()

fun Int.spToPx() : Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()


fun Fragment.hideKeyboard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)

    //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun AppCompatActivity.hideKeyboard() {
    val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

fun Fragment.showKeyboard() {
    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun AppCompatActivity.showKeyboard() {
    val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//    inputMethodManager.showSoftInput(this.currentFocus, 0)
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun ScrollView.isViewVisible(view: View): Boolean {
    val scrollBounds = Rect()
    this.getDrawingRect(scrollBounds)
    val top: Float = view.y
    val bottom: Float = top + view.height
    return scrollBounds.top < top && scrollBounds.bottom > bottom
}