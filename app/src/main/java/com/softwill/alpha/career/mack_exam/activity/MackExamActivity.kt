package com.softwill.alpha.career.mack_exam.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.career.mack_exam.adapter.MockExamAdapter
import com.softwill.alpha.career.mack_exam.adapter.MockExamFacultiesAdapter
import com.softwill.alpha.career.mack_exam.model.MockExamModel
import com.softwill.alpha.databinding.ActivityMackExamBinding
import com.softwill.alpha.institute.online_exam.student.activity.StudentExam2Activity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MackExamActivity : AppCompatActivity(), MockExamAdapter.CallbackInterface,
    MockExamFacultiesAdapter.MockExamFacultiesCallbackInterface {

    private lateinit var binding: ActivityMackExamBinding

    var mMockExamFacultiesAdapter: MockExamFacultiesAdapter? = null
    var mMockExamAdapter: MockExamAdapter? = null

    val mFacilitiesList = ArrayList<FacultyModel2>()
    val mMockExamModel = java.util.ArrayList<MockExamModel>()

    var mFacultyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mack_exam)
        setupBack()

        setupAdapter()

        apiFaculties();
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {

        mMockExamAdapter = MockExamAdapter(mMockExamModel, this@MackExamActivity, this)
        binding.rvMockExam.adapter = mMockExamAdapter
        mMockExamAdapter?.notifyDataSetChanged()

        mMockExamFacultiesAdapter =
            MockExamFacultiesAdapter(mFacilitiesList, this@MackExamActivity, this)
        binding.rvMockFaculties.adapter = mMockExamFacultiesAdapter
        mMockExamFacultiesAdapter?.notifyDataSetChanged()
    }





    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_mack_exam)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.mock_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_search -> {
                val intent = Intent(applicationContext, SearchMockExamActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_result -> {
                val intent = Intent(applicationContext, MockExamResultActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSolveCallback(pos: Int, examId: Int, subject: String) {
        addConfirmationBottomSheet("MockExam", examId, subject)
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

    private fun apiFaculties() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@MackExamActivity).myApi.api_Faculties()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val parsedData = UtilsFunctions().parseFacultyModel2Json(responseJson)
                        if (parsedData != null) {
                            mFacilitiesList.clear()
                            mFacilitiesList.addAll(parsedData)

                            if (mFacilitiesList.isNotEmpty()) {
                                binding.rvMockFaculties.visibility = View.VISIBLE
                                mMockExamFacultiesAdapter?.notifyDataSetChanged()
                                mFacultyId = mFacilitiesList[0].facultyId
                                apiMockExamList(mFacultyId)
                            }
                        } else {
                            binding.rvMockFaculties.visibility = View.GONE
                        }
                    } else {

                        binding.rvMockFaculties.visibility = View.GONE

                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@MackExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun itemClickCallback(facultyId: Int, position: Int) {
        mFacultyId = facultyId
        apiMockExamList(mFacultyId)
    }


    private fun apiMockExamList(facultyId: Int) {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@MackExamActivity)
            .myApi.api_MockExamList(facultyId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<MockExamModel>>() {}.type
                        val mList: List<MockExamModel> = Gson().fromJson(responseJson, listType)

                        // Update your mEntranceExamList with the new data
                        mMockExamModel.clear()
                        mMockExamModel.addAll(mList)

                        if (mMockExamModel.isNotEmpty()) {
                            binding.tvExam.visibility = View.VISIBLE
                            binding.rvMockExam.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mMockExamAdapter?.notifyDataSetChanged()
                        } else {
                            binding.tvExam.visibility = View.GONE
                            binding.rvMockExam.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.tvExam.visibility = View.GONE
                        binding.rvMockExam.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@MackExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        apiMockExamList(mFacultyId)
    }

}