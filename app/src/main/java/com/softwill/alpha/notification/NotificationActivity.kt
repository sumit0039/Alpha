package com.softwill.alpha.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityNotificationBinding
import com.softwill.alpha.notification.request.fragment.RequestFragment


class NotificationActivity : AppCompatActivity(), RequestFragment.RequestFragmentInterface {


    private lateinit var binding: ActivityNotificationBinding
    lateinit var tvCount : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_notification)


        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(com.softwill.alpha.R.string.title_notifications)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)

        setTabLayout()

    }

    private fun setTabLayout() {

        val tabZero = LayoutInflater.from(this)
            .inflate(com.softwill.alpha.R.layout.item_tab, null) as LinearLayout
        val tvTitle = tabZero.findViewById<TextView>(R.id.tvTitle)
        tvCount = tabZero.findViewById(R.id.tvCount)
        val cardView = tabZero.findViewById<CardView>(R.id.cardView)
        tvTitle.text = resources.getString(com.softwill.alpha.R.string.request)
        tvCount.text = "0"
        binding.tabLayout.getTabAt(0)?.customView = tabZero


        val tabOne = LayoutInflater.from(this)
            .inflate(com.softwill.alpha.R.layout.item_tab, null) as LinearLayout
        val tvTitle1 = tabOne.findViewById<TextView>(R.id.tvTitle)
        val tvCount1 = tabOne.findViewById<TextView>(R.id.tvCount)
        val cardView1 = tabOne.findViewById<CardView>(R.id.cardView)
        tvTitle1.text = resources.getString(com.softwill.alpha.R.string.general)
        tvCount1.text = "8"
        binding.tabLayout.getTabAt(1)?.customView = tabOne

        tvTitle.setTextColor(resources.getColor(R.color.primary_color))
        tvCount.setTextColor(resources.getColor(R.color.white))
        cardView.setCardBackgroundColor(resources.getColor(R.color.primary_color))

        tvTitle1.setTextColor(resources.getColor(R.color.grey_light2))
        tvCount1.setTextColor(resources.getColor(R.color.black))
        cardView1.setCardBackgroundColor(resources.getColor(R.color.gray_light))

        val adapter = MyNotificationTabAdapter(this, supportFragmentManager, 2)
        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                if (tab.position == 0) {
                    tvTitle.setTextColor(resources.getColor(R.color.primary_color))
                    tvCount.setTextColor(resources.getColor(R.color.white))
                    cardView.setCardBackgroundColor(resources.getColor(R.color.primary_color))
                } else {
                    tvTitle1.setTextColor(resources.getColor(R.color.primary_color))
                    tvCount1.setTextColor(resources.getColor(R.color.white))
                    cardView1.setCardBackgroundColor(resources.getColor(R.color.primary_color))
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    tvTitle.setTextColor(resources.getColor(R.color.grey_light2))
                    tvCount.setTextColor(resources.getColor(R.color.black))
                    cardView.setCardBackgroundColor(resources.getColor(R.color.gray_light))
                } else {
                    tvTitle1.setTextColor(resources.getColor(R.color.grey_light2))
                    tvCount1.setTextColor(resources.getColor(R.color.black))
                    cardView1.setCardBackgroundColor(resources.getColor(R.color.gray_light))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun requestsCount(count: Int) {
        tvCount.text = ""
        tvCount.text = count.toString()
    }


}

