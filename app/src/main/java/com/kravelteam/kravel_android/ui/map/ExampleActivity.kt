package com.kravelteam.kravel_android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.GlideApp
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import kotlinx.android.synthetic.main.activity_example.*
import timber.log.Timber

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        Timber.e(intent.getStringExtra("imgEx"))
        intent.getStringExtra("imgEx")?.let {
            GlideApp.with(this).load(it).into(img_example)
        }

        img_example_cancel.setOnDebounceClickListener {
            finish()
        }
    }
}