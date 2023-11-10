package com.softwill.alpha.profile.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityActivityBinding

class ActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActivityBinding
    var mActivityAssignmentsAdapter: ActivityAssignmentsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activity)


        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_activity)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)


        val data = ArrayList<ActivityItemModel>()
        data.add(ActivityItemModel("Pending assignments"))
        data.add(ActivityItemModel("Pending exam"))
        data.add(ActivityItemModel("Pending lecture"))
        data.add(ActivityItemModel("Pending assignments"))
        data.add(ActivityItemModel("Pending exam"))
        data.add(ActivityItemModel("Pending lecture"))
        data.add(ActivityItemModel("Pending assignments"))
        data.add(ActivityItemModel("Pending exam"))
        data.add(ActivityItemModel("Pending lecture"))


        mActivityAssignmentsAdapter = ActivityAssignmentsAdapter(data, applicationContext)
        binding.rvActivity.adapter = mActivityAssignmentsAdapter
        mActivityAssignmentsAdapter!!.notifyDataSetChanged()

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
}