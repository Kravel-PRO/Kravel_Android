package com.hyesun.kravel_android.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hyesun.kravel_android.ui.search.CelebFragment
import com.hyesun.kravel_android.ui.search.DramaFragment

class SearchViewPagerAdapter (fm: FragmentManager, private val size: Int, private val title: ArrayList<String>) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getCount(): Int = size

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CelebFragment()
            else -> DramaFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}