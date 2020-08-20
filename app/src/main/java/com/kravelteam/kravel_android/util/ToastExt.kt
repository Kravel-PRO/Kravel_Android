package com.kravelteam.kravel_android.util

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
}

fun Application.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.networkErrorToast() {
    toast("네트워크 에러가 발생하였습니다. 다시 시도해주세요.")
}

fun Fragment.networkErrorToast() {
    toast("네트워크 에러가 발생하였습니다. 다시 시도해주세요.")
}