package com.kravelteam.kravel_android.util

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kravelteam.kravel_android.R

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
    toast(resources.getString(R.string.errorNetwork))
}

fun Fragment.networkErrorToast() {
    toast(resources.getString(R.string.errorNetwork))
}

fun Application.networkErrorToast() {
    toast(resources.getString(R.string.errorNetwork))
}