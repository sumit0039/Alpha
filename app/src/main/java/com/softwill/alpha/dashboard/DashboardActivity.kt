package com.softwill.alpha.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityDashboardBinding


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val adapter = MyDashboardTabAdapter(this, supportFragmentManager, 2)
        binding.viewPager.adapter = adapter

        binding.viewPager.setCurrentItem(1, true)


    }





    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }




}