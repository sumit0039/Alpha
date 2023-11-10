package com.softwill.alpha.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.softwill.alpha.profile.tabAbout.AboutFragment
import com.softwill.alpha.profile.tabActivity.ActivityFragment

class ProfileTabAdapter(
    private val myContext: ProfileFragment,
    fm: FragmentManager,
    private var totalTabs: Int,
) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return ActivityFragment.newInstance(false, -1)
            }
            1 -> {
                return AboutFragment.newInstance(false, -1)
            }
            else -> return ActivityFragment.newInstance(false, -1)
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}