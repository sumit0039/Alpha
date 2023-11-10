package com.softwill.alpha.institute.classes.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.softwill.alpha.institute.classes.fragment.ClassesInfoFragment
import com.softwill.alpha.institute.classes.fragment.ClassesPendingFragment
import com.softwill.alpha.institute.classes.fragment.ClassesSubjectFragment


class ClassesTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    private var totalTabs: Int,
    mStudentId: Int
) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return ClassesInfoFragment()
            }
            1 -> {
                return ClassesSubjectFragment()
            }
            2 -> {
                return ClassesPendingFragment()
            }
            else -> return ClassesInfoFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}