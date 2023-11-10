package com.softwill.alpha.institute.online_exam.student.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityStudentExamBinding
import com.softwill.alpha.institute.library.activity.SavedActivity
import com.softwill.alpha.institute.online_exam.student.adapter.StudentExamCompletedAdapter
import com.softwill.alpha.institute.online_exam.student.adapter.StudentExamOngoingAdapter
import com.softwill.alpha.institute.online_exam.student.adapter.StudentExamOngoingAdapter.AdapterCallbackInterface
import com.softwill.alpha.institute.online_exam.student.model.StudentCompletedExam
import com.softwill.alpha.institute.online_exam.student.model.StudentOngoingExam
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentExamActivity : AppCompatActivity(), AdapterCallbackInterface {

    private lateinit var binding: ActivityStudentExamBinding
    private var mStudentExamCompletedAdapter: StudentExamCompletedAdapter? = null
    private var mStudentExamOngoingAdapter: StudentExamOngoingAdapter? = null


    val mStudentOngoingExam = java.util.ArrayList<StudentOngoingExam>()
    val mStudentCompletedExam = java.util.ArrayList<StudentCompletedExam>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_student_exam)


        setupBack()
        onClickListener()
        setupAdapter()

        apiStudentOngoingExam()
    }

    private fun setupAdapter() {
        mStudentExamOngoingAdapter =
            StudentExamOngoingAdapter(this@StudentExamActivity, mStudentOngoingExam, this)
        binding.rvOngoingExam.adapter = mStudentExamOngoingAdapter
        mStudentExamOngoingAdapter!!.notifyDataSetChanged()


        mStudentExamCompletedAdapter = StudentExamCompletedAdapter(applicationContext, mStudentCompletedExam)
        binding.rvCompletedExam.adapter = mStudentExamCompletedAdapter
        mStudentExamCompletedAdapter!!.notifyDataSetChanged()
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



            apiStudentOngoingExam()

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


            apiStudentCompletedExam()
        }


    }





    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.exam)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
            com.softwill.alpha.R.id.menu_save -> {
                val intent = Intent(applicationContext, SavedActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSolveCallback(position: Int, examId: Int, subject: String) {
        addConfirmationBottomSheet("OnlineExam", examId, subject)
    }

    private fun addConfirmationBottomSheet(type: String, examId: Int, subject: String) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_start_exam, null)


        val subTitle = view.findViewById<TextView>(R.id.textView10)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)


        if (type == "OnlineExam") {
            subTitle.text = resources.getText(R.string.do_you_want_to_start_the_exam)
        } else {
            subTitle.text = resources.getText(R.string.do_you_want_to_start_the_mock_exam)
        }


        btnYes.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(applicationContext, StudentExam2Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra("mFrom", type)
            intent.putExtra("mExamId", examId)
            intent.putExtra("mSubject", subject)
            startActivity(intent)
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun apiStudentOngoingExam() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@StudentExamActivity).myApi.api_StudentOngoingExam()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<StudentOngoingExam>>() {}.type
                        val mList: List<StudentOngoingExam> =
                            Gson().fromJson(responseBody, listType)

                        mStudentOngoingExam.clear()
                        mStudentOngoingExam.addAll(mList)


                        if (mStudentOngoingExam.isNotEmpty()) {
                            mStudentExamOngoingAdapter?.notifyDataSetChanged()
                            binding.rvOngoingExam.visibility = View.VISIBLE
                            binding.rvCompletedExam.visibility = View.GONE
                        } else {
                            binding.rvOngoingExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvOngoingExam.visibility = View.GONE
                    binding.rvCompletedExam.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@StudentExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    private fun apiStudentCompletedExam() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@StudentExamActivity).myApi. api_StudentCompletedExam()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<StudentCompletedExam>>() {}.type
                        val mList: List<StudentCompletedExam> =
                            Gson().fromJson(responseBody, listType)

                        mStudentCompletedExam.clear()
                        mStudentCompletedExam.addAll(mList)


                        if (mStudentCompletedExam.isNotEmpty()) {
                            mStudentExamCompletedAdapter?.notifyDataSetChanged()
                            binding.rvOngoingExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.VISIBLE
                        } else {
                            binding.rvOngoingExam.visibility = View.GONE
                            binding.rvCompletedExam.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvOngoingExam.visibility = View.GONE
                    binding.rvCompletedExam.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@StudentExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



}