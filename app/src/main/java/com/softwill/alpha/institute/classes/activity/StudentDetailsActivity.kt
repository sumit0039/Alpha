package com.softwill.alpha.institute.classes.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.softwill.alpha.R
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.databinding.ActivityStudentDetailsBinding
import com.softwill.alpha.institute.classes.adapter.ClassesTabAdapter
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity

class StudentDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailsBinding

    private var mUserId: Int = -1
    private var mStudentId: Int = -1
    private var mName: String? = null
    private var mUserName: String? = null
    private var mProfileImage: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_details)


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUserId = bundle.getInt("mUserId")
            mStudentId = bundle.getInt("mStudentId")
            mName = bundle.getString("mName").toString()
            mUserName = bundle.getString("mUserName").toString()
            mProfileImage = bundle.getString("mProfileImage").toString()

        }

        Glide.with(this).load(mProfileImage).placeholder(R.drawable.icon_no_image).into(binding.ivProfileImage)
        binding.tvName.text = mName
        binding.tvUserName.text = mUserName

        setupBack()
        onClickListener()


        setTabLayout()

    }
    private fun setTabLayout() {
        val adapter = ClassesTabAdapter(this, supportFragmentManager, 3, mStudentId)
        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }


    private fun onClickListener() {
        binding.card1.setOnClickListener {
            val intent = Intent(applicationContext, ProfileGuestActivity::class.java)
            intent.putExtra("mUserId", mUserId)
            startActivity(intent)
        }


        binding.card2.setOnClickListener {
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("mName", mName)
            startActivity(intent)
        }

    }




    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.student)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getStudentId(): Int? {
        return mStudentId
    }
}
