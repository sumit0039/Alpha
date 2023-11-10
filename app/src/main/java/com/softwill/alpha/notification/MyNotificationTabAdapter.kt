package com.softwill.alpha.notification

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.softwill.alpha.notification.general.GeneralFragment
import com.softwill.alpha.notification.request.fragment.RequestFragment


class MyNotificationTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int
) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return RequestFragment()
            }
            1 -> {
                return GeneralFragment()
            }
            else -> return RequestFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}