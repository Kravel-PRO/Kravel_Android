package com.hyesun.kravel_android.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.common.setOnDebounceClickListener
import com.hyesun.kravel_android.ui.adapter.MainViewPagerAdapter
import com.hyesun.kravel_android.ui.home.HomeFragment
import com.hyesun.kravel_android.ui.map.MapFragment
import com.hyesun.kravel_android.ui.mypage.UserFragment
import com.hyesun.kravel_android.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val list : List<Menus> by lazy {
        listOf(
            Menus(cl_main_home, img_main_home, HomeFragment()),
            Menus(cl_main_search, img_main_search, SearchFragment()),
            Menus(cl_main_map, img_main_map, MapFragment()),
            Menus(cl_main_my_page,img_main_my_page,UserFragment())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            Menus(cl_main_map, img_main_map, MapFragment()),
            Menus(cl_main_my_page,img_main_my_page,UserFragment())
        )

        list.forEachIndexed { index, menus ->
            menus.img.isSelected = index == tabNum
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main, list[tabNum].fragment)
            .commitAllowingStateLoss()
    }

    data class Menus (
        val layout: ConstraintLayout,
        val img: ImageView,
        val fragment: Fragment
    )

}
