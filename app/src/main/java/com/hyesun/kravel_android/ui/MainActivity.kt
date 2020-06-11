package com.hyesun.kravel_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.hyesun.kravel_android.R
import com.hyesun.kravel_android.adapter.MainViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewPager()
    }
    fun initViewPager() {
        viewpager_main.adapter = MainViewPagerAdapter(supportFragmentManager)
        viewpager_main.offscreenPageLimit = 3
        viewpager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                bottom_main.menu.getItem(position).isChecked = true
            }

        })
        bottom_main.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.menu_home-> viewpager_main.currentItem =0
                R.id.menu_search -> viewpager_main.currentItem =1
                R.id.menu_map-> viewpager_main.currentItem =2
                R.id.menu_user-> viewpager_main.currentItem=3
            }
            true
        }
    }


}
