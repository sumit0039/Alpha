package com.softwill.alpha.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.softwill.alpha.R
import com.softwill.alpha.career.CareerFragment
import com.softwill.alpha.chat.ChatFragment
import com.softwill.alpha.databinding.FragmentDashboardBinding
import com.softwill.alpha.home.fragment.HomeFragment
import com.softwill.alpha.institute.InstituteFragment
import com.softwill.alpha.profile.ProfileFragment

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_institute -> {
                    loadFragment(InstituteFragment())
                    true
                }
                R.id.nav_career -> {
                    loadFragment(CareerFragment())
                    true
                }
                R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> {
                    loadFragment(HomeFragment())
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

}