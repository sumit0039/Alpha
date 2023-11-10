package com.softwill.alpha.dashboard

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyDashboardTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int
) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return ReportFragment()
            }
            1 -> {
                return DashboardFragment()
            }
            else -> return DashboardFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}