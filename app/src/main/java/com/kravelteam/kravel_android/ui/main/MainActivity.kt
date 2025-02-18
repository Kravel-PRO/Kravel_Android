package com.kravelteam.kravel_android.ui.main

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.ui.home.HomeFragment
import com.kravelteam.kravel_android.ui.map.MapViewFragment
import com.kravelteam.kravel_android.ui.map.fragmentBackPressed
import com.kravelteam.kravel_android.ui.mypage.UserFragment
import com.kravelteam.kravel_android.ui.search.SearchFragment
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private val list : List<Menus> by lazy {
        listOf(
            Menus(cl_main_home, img_main_home, HomeFragment()),
            Menus(cl_main_search, img_main_search, SearchFragment()),
            Menus(cl_main_map, img_main_map, MapViewFragment()),
            Menus(cl_main_my_page, img_main_my_page, UserFragment())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setResult(Activity.RESULT_OK)
        initSetting()
    }

    private fun initSetting(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_main, HomeFragment())
            .commitAllowingStateLoss()

        img_main_home.isSelected = true

        list.forEachIndexed { index, menus ->
            menus.layout.setOnDebounceClickListener { changeSelected(index) }
        }

    }

    //selected
    fun changeSelected(tabNum: Int){
        val list = listOf(
            Menus(cl_main_home, img_main_home, HomeFragment()),
            Menus(cl_main_search, img_main_search, SearchFragment()),
            Menus(cl_main_map, img_main_map, MapViewFragment()),
            Menus(cl_main_my_page, img_main_my_page, UserFragment())
        )

        list.forEachIndexed { index, menus ->
            menus.img.isSelected = index == tabNum
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main, list[tabNum].fragment)
            .commitAllowingStateLoss()
        checkFragment = false
    }

    data class Menus(
        val layout: ConstraintLayout,
        val img: ImageView,
        val fragment: Fragment
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }
    companion object{
        private var backKeyPressedTime : Long = 0
        private var checkFragment : Boolean = false
    }

    override fun onBackPressed() {
      //  super.onBackPressed()
        val fragment = this.supportFragmentManager.findFragmentById(R.id.fl_main)

        (fragment as? fragmentBackPressed)?.onBackPressed()?.not()?.let {
            if (it == false) {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis()
                    this.toast(resources.getString(R.string.backbuttonWarning))
                    return
                } else {
                    this.finish()
                }
            }
        }
    }

}
