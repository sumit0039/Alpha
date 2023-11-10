package com.softwill.alpha.profile_guest.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.softwill.alpha.profile.tabAbout.AboutFragment
import com.softwill.alpha.profile.tabActivity.ActivityFragment

class ProfileGuestTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int,
    private val mUserId : Int
) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return ActivityFragment.newInstance(true, mUserId)
                //return ActivityFragment(mVisitor)
            }
            1 -> {
                return AboutFragment.newInstance(true, mUserId)
            }
            else ->  return ActivityFragment.newInstance(true, mUserId)
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}