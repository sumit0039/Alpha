package com.softwill.alpha.institute.assignment.student.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityStudentAssignmentBinding
import com.softwill.alpha.institute.assignment.student.adapter.SAssignmentCompletedAdapter
import com.softwill.alpha.institute.assignment.student.adapter.SAssignmentOngoingAdapter
import com.softwill.alpha.institute.assignment.student.model.StudentCompletedAssignment
import com.softwill.alpha.institute.assignment.student.model.StudentOngoingAssignment
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentAssignmentActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStudentAssignmentBinding
    var mSAssignmentOngoingAdapter: SAssignmentOngoingAdapter? = null
    private var mSAssignmentCompletedAdapter: SAssignmentCompletedAdapter? = null

    val mStudentOngoingAssignment = ArrayList<StudentOngoingAssignment>()
    private val mStudentCompletedAssignment = ArrayList<StudentCompletedAssignment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_student_assignment
        )


        setupBack()
        onClickListener()
        setupAdapter()

        apiStudentOngoingAssignment()
    }

    private fun setupAdapter() {

        mSAssignmentCompletedAdapter = SAssignmentCompletedAdapter(this@StudentAssignmentActivity, mStudentCompletedAssignment)
        binding.rvSCompletedAssignment.adapter = mSAssignmentCompletedAdapter
        mSAssignmentCompletedAdapter!!.notifyDataSetChanged()


        mSAssignmentOngoingAdapter = SAssignmentOngoingAdapter(this@StudentAssignmentActivity, mStudentOngoingAssignment)
        binding.rvSOngoingAssignment.adapter = mSAssignmentOngoingAdapter
        mSAssignmentOngoingAdapter!!.notifyDataSetChanged()
    }



    private fun onClickListener() {


        binding.tvOngoing.setOnClickListener {
            binding.tvOngoing.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            binding.tvOngoing.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvCompleted.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvCompleted.setBackgroundResource(R.drawable.bg_rounded_3)

            apiStudentOngoingAssignment()

        }

        binding.tvCompleted.setOnClickListener {
            binding.tvCompleted.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            binding.tvCompleted.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvOngoing.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvOngoing.setBackgroundResource(R.drawable.bg_rounded_3)



            apiStudentCompletedAssignment()
        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_assignment)
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



    private fun apiStudentOngoingAssignment() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@StudentAssignmentActivity).myApi.api_StudentOngoingAssignment()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<StudentOngoingAssignment>>() {}.type
                        val mList: List<StudentOngoingAssignment> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mStudentOngoingAssignment.clear()
                        mStudentOngoingAssignment.addAll(mList)


                        if (mStudentOngoingAssignment.isNotEmpty()) {
                            mSAssignmentOngoingAdapter?.notifyDataSetChanged()
                            binding.rvSOngoingAssignment.visibility = View.VISIBLE
                            binding.rvSCompletedAssignment.visibility = View.GONE
                        } else {
                            binding.rvSOngoingAssignment.visibility = View.GONE
                            binding.rvSCompletedAssignment.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvSOngoingAssignment.visibility = View.GONE
                    binding.rvSCompletedAssignment.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@StudentAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiStudentCompletedAssignment() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@StudentAssignmentActivity).myApi.api_StudentCompletedAssignment()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<StudentCompletedAssignment>>() {}.type
                        val mList: List<StudentCompletedAssignment> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mStudentCompletedAssignment.clear()
                        mStudentCompletedAssignment.addAll(mList)


                        if (mStudentCompletedAssignment.isNotEmpty()) {
                            mSAssignmentCompletedAdapter?.notifyDataSetChanged()
                            binding.rvSOngoingAssignment.visibility = View.GONE
                            binding.rvSCompletedAssignment.visibility = View.VISIBLE
                        } else {
                            binding.rvSOngoingAssignment.visibility = View.GONE
                            binding.rvSCompletedAssignment.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvSOngoingAssignment.visibility = View.GONE
                    binding.rvSCompletedAssignment.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@StudentAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}