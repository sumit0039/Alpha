package com.softwill.alpha.institute.attendance.activity

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityAttendanceTeacherBinding
import com.softwill.alpha.institute.attendance.adapter.AttendanceTeacherAdapter
import com.softwill.alpha.utils.Constant

class AttendanceTeacherActivity : AppCompatActivity(),
    LectureClassAdapter.LectureClassCallbackInterface {

    private lateinit var binding: ActivityAttendanceTeacherBinding
    var mLectureClassAdapter: LectureClassAdapter? = null
    private var mDelayHandler: Handler? = null
    var mAttendanceTeacherAdapter: AttendanceTeacherAdapter? = null

    val mLectureClassModel = java.util.ArrayList<LectureClassModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_attendance_teacher)



        setupBack()
        setupClassAdapter()
        setupSwipeListener()
        setupAdapter()
    }
    private fun setupAdapter() {


        mAttendanceTeacherAdapter = AttendanceTeacherAdapter(applicationContext)
        binding.rvAttendance.adapter = mAttendanceTeacherAdapter
        mAttendanceTeacherAdapter!!.notifyDataSetChanged()


    }

    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        Toast.makeText(applicationContext, "Updated!!", Toast.LENGTH_SHORT).show()
    }


    private fun setupClassAdapter() {

        mLectureClassAdapter = LectureClassAdapter(this@AttendanceTeacherActivity, mLectureClassModel, this)
        binding.rvClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()
    }



    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_attendance)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className :String) {
        TODO("Not yet implemented")
    }
}