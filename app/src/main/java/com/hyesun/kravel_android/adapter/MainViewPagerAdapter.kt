package com.hyesun.kravel_android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hyesun.kravel_android.ui.fragment.HomeFragment
import com.hyesun.kravel_android.ui.fragment.MapFragment
import com.hyesun.kravel_android.ui.fragment.SearchFragment
import com.hyesun.kravel_android.ui.fragment.UserFragment

class MainViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int =5

    override fun getItem(position: Int): Fragment {
        return when(position)
        {
            0-> HomeFragment()
            1-> SearchFragment()
            2-> MapFragment()
            else-> UserFragment()
        }
    }
}