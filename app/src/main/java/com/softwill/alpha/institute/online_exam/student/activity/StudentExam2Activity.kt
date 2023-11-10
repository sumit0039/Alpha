package com.softwill.alpha.institute.online_exam.student.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.model.ExamModel
import com.softwill.alpha.career.mack_exam.model.QuestionModel
import com.softwill.alpha.databinding.ActivityStudentExam2Binding
import com.softwill.alpha.institute.online_exam.teacher.adapter.PagerObjectiveAdapter
import com.softwill.alpha.institute.online_exam.teacher.adapter.TeacherExamQuestionsAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.DetailOnPageChangeListener
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudentExam2Activity : AppCompatActivity(),
    PagerObjectiveAdapter.PagerObjectiveCallbackInterface {

    private lateinit var binding: ActivityStudentExam2Binding
    var mTeacherExamQuestionsAdapter: TeacherExamQuestionsAdapter? = null

    private val mQuestionModel = ArrayList<QuestionModel>()
    private val onPageChangeListener = DetailOnPageChangeListener()


    private var mFrom: String? = null
    var mExamId: Int = 0
    private var mSubject: String? = null

    var mPagerObjectiveAdapter: PagerObjectiveAdapter? = null

    var remainingCount = 0
    var solvedCount = 0

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_student_exam2
            )


        onClickListener()
        setupCreateExamListAdapter()


        val bundle: Bundle? = intent.extras
        mFrom = bundle?.getString("mFrom")
        mExamId = bundle!!.getInt("mExamId")
        mSubject = bundle.getString("mSubject")

        setupBack()


        apiExamDetails()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCreateExamListAdapter() {

        mTeacherExamQuestionsAdapter =
            TeacherExamQuestionsAdapter(this@StudentExam2Activity, mQuestionModel)
        binding.rvQuestions.adapter = mTeacherExamQuestionsAdapter
        mTeacherExamQuestionsAdapter!!.notifyDataSetChanged()

    }

    private fun apiExamDetails() {

        val retrofit = RetrofitClient.getInstance(this@StudentExam2Activity).myApi
        val call: Call<ResponseBody> = when (mFrom) {
            "MockExam" -> retrofit.api_MockExamDetail(mExamId)
            else -> retrofit.api_StudentExamStart(mExamId)
        }


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {

                        val exam = Gson().fromJson(responseJson, ExamModel::class.java)

                        binding.tvTotalQuestions.text = exam.exam_questions.size.toString()


                        if (exam.exam_questions.isNotEmpty()) {

                            mQuestionModel.clear()
                            mQuestionModel.addAll(exam.exam_questions)
                            mTeacherExamQuestionsAdapter?.notifyDataSetChanged()


                            mPagerObjectiveAdapter = PagerObjectiveAdapter(
                                this@StudentExam2Activity,
                                mQuestionModel,
                                this@StudentExam2Activity
                            )
                            binding.pagerObjectiveQuestion.addOnPageChangeListener(
                                onPageChangeListener
                            )
                            binding.pagerObjectiveQuestion.adapter = mPagerObjectiveAdapter
                            mPagerObjectiveAdapter!!.notifyDataSetChanged()
                            binding.pagerObjectiveQuestion.setOnTouchListener { _, _ -> true }



                            binding.tvQuestionNo.text =
                                (onPageChangeListener.currentPage + 1).toString()

                            remainingCount = mQuestionModel.size
                            binding.tvRemainingQues.text = remainingCount.toString()
                        }


                    } else {
                        UtilsFunctions().showToast(this@StudentExam2Activity, "No data found !!")
                        finish()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@StudentExam2Activity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    @SuppressLint("SetTextI18n")
    private fun onClickListener() {
        binding.tvPrevious.setOnClickListener {
            binding.llQuestion.visibility = View.VISIBLE
            binding.llQuestionList.visibility = View.GONE
        }


        binding.tvQuestion.setOnClickListener {
            binding.llQuestion.visibility = View.GONE
            binding.llQuestionList.visibility = View.VISIBLE
        }



        binding.btnPreviousQue.setOnClickListener {
            if (onPageChangeListener.currentPage > 0) {
                binding.pagerObjectiveQuestion.setCurrentItem(
                    onPageChangeListener.currentPage - 1,
                    true
                )

                binding.tvQuestionNo.text = (onPageChangeListener.currentPage + 1).toString()
            }

        }

        binding.btnNextQue.setOnClickListener {
            if (onPageChangeListener.currentPage < mQuestionModel.size) {
                binding.pagerObjectiveQuestion.setCurrentItem(
                    onPageChangeListener.currentPage + 1,
                    true
                );

                binding.tvQuestionNo.text = (onPageChangeListener.currentPage + 1).toString()
            }
        }




        binding.btnSubmit.setOnClickListener {
            if (solvedCount == mQuestionModel.size) {
                addConfirmationBottomSheet()

            } else {
                UtilsFunctions().showToast(
                    this@StudentExam2Activity,
                    "Please complete remaining questions"
                )
            }
        }

    }

    private fun addConfirmationBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_confirmation_exam, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog.dismiss()
            apiSubmitExam()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mSubject
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onAnswerCallback(position: Int, answerId: Int) {
        mQuestionModel[position].selectedAnswerId = answerId
        mTeacherExamQuestionsAdapter!!.notifyDataSetChanged()

        remainingCount = mQuestionModel.count { it.selectedAnswerId == 0 }
        solvedCount = mQuestionModel.count { it.selectedAnswerId != 0 }

        binding.tvSolvedQues.text = solvedCount.toString()
        binding.tvRemainingQues.text = remainingCount.toString()

    }

    private fun apiSubmitExam() {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@StudentExam2Activity)

        val jsonObject = JsonObject()


        val jsonArray = JsonArray()
        for (questionModel in mQuestionModel) {
            /*if (questionModel.selectedAnswerId != 0) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("id", questionModel.id)
                jsonObject.addProperty("selectedAnswerId", questionModel.selectedAnswerId)
                jsonArray.add(jsonObject)
            } else {
                progressDialog?.dismiss()
                UtilsFunctions().showToast(
                    this@StudentExam2Activity,
                    "Please complete remaining questions"
                )
            }*/

            val questionObject = JsonObject()
            questionObject.addProperty("id", questionModel.id)
            questionObject.addProperty("selectedAnswerId", questionModel.selectedAnswerId)
            jsonArray.add(questionObject)
        }
        jsonObject.add("exam_questions", jsonArray)


        val retrofit = RetrofitClient.getInstance(this@StudentExam2Activity).myApi
        val call: Call<ResponseBody> = when (mFrom) {
            "MockExam" -> retrofit.api_SubmitMockExam(mExamId, jsonObject)
            else -> retrofit.api_StudentSubmitExam(mExamId, jsonObject)
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Mock exam submit successfully" || message == "Assignment submit successfully") {
                            UtilsFunctions().showToast(this@StudentExam2Activity, message)
                            finish()
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@StudentExam2Activity, responseObject.getString("error")
                        )
                    }

                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@StudentExam2Activity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }


}