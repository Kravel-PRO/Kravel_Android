package com.kravelteam.kravel_android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.ui.adapter.CameraModeRecyclerView
import java.lang.reflect.Array.newInstance

class CameraActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
//
//        savedInstanceState ?: supportFragmentManager.beginTransaction()
//            .replace(R.id.container, CameraFragment.newInstance())
//            .commit()
    }

}