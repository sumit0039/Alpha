package com.softwill.alpha.institute.assignment.student.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityAssignmentQuestionsBinding
import com.softwill.alpha.institute.assignment.student.adapter.AssignmentPagerAdapter
import com.softwill.alpha.institute.assignment.student.adapter.StudentAssignmentQuestionsAdapter
import com.softwill.alpha.institute.assignment.student.model.AssignmentModel
import com.softwill.alpha.institute.assignment.student.model.AssignmentQuestionModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.DetailOnPageChangeListener
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignmentQuestionsActivity : AppCompatActivity(),
    AssignmentPagerAdapter.AssignmentPagerCallbackInterface {


    private lateinit var binding: ActivityAssignmentQuestionsBinding
    private var mStudentAssignmentQuestionsAdapter: StudentAssignmentQuestionsAdapter? = null
    private var mSubjectName: String? = null
    private var mExamId: Int = 0

    private val mAssignmentQuestionModel = ArrayList<AssignmentQuestionModel>()
    private val onPageChangeListener = DetailOnPageChangeListener()
    var mAssignmentPagerAdapter: AssignmentPagerAdapter? = null
    var mRemainingCount = 0
    var mSolvedCount = 0

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_assignment_questions
            )


        val bundle: Bundle? = intent.extras
        mExamId = bundle?.getInt("mExamId")!!
        mSubjectName = bundle.getString("mSubjectName")




        setupBack()
        onClickListener()
        setupCreateExamListAdapter()

        apiStudentStartAssignment()


    }

    private fun setupCreateExamListAdapter() {

        mStudentAssignmentQuestionsAdapter = StudentAssignmentQuestionsAdapter(
            this@AssignmentQuestionsActivity,
            mAssignmentQuestionModel
        )
        binding.rvQuestions.adapter = mStudentAssignmentQuestionsAdapter
        mStudentAssignmentQuestionsAdapter!!.notifyDataSetChanged()


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


        binding.btnSubmit.setOnClickListener {
            if (mSolvedCount == mAssignmentQuestionModel.size) {
                addConfirmationBottomSheet()

            } else {
                UtilsFunctions().showToast(
                    this@AssignmentQuestionsActivity,
                    "Please complete remaining questions"
                )
            }
        }


        binding.btnPreviousQue.setOnClickListener {
            if (onPageChangeListener.currentPage > 0) {
                binding.pagerObjectiveQuestion.setCurrentItem(
                    onPageChangeListener.currentPage - 1,
                    true
                )

                binding.tvQuestionNo.text = (onPageChangeListener.currentPage + 1).toString().padStart(2, '0')
            }

        }


        binding.btnNextQue.setOnClickListener {
            if (onPageChangeListener.currentPage < mAssignmentQuestionModel.size) {
                binding.pagerObjectiveQuestion.setCurrentItem(
                    onPageChangeListener.currentPage + 1,
                    true
                );

                binding.tvQuestionNo.text = (onPageChangeListener.currentPage + 1).toString().padStart(2, '0')
            }
        }
    }

    private fun addConfirmationBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_confirmation_assignment, null)


        val tvNo = view.findViewById<TextView>(R.id.tvNo)
        val tvYes = view.findViewById<TextView>(R.id.tvYes)

        tvYes.setOnClickListener {
            dialog.dismiss()
            apiSubmitExam()
        }

        tvNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mSubjectName.toString()
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


    private fun apiStudentStartAssignment() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@AssignmentQuestionsActivity)
            .myApi.api_StudentStartAssignment(mExamId)


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {

                        val exam = Gson().fromJson(responseJson, AssignmentModel::class.java)

                        //binding.tvTotalQuestions.text = exam.exam_questions.size.toString()


                        if (exam.assignment_questions.isNotEmpty()) {

                            mAssignmentQuestionModel.clear()
                            mAssignmentQuestionModel.addAll(exam.assignment_questions)
                            mStudentAssignmentQuestionsAdapter?.notifyDataSetChanged()


                            mAssignmentPagerAdapter = AssignmentPagerAdapter(
                                this@AssignmentQuestionsActivity,
                                mAssignmentQuestionModel,
                                this@AssignmentQuestionsActivity
                            )
                            binding.pagerObjectiveQuestion.addOnPageChangeListener(
                                onPageChangeListener
                            )
                            binding.pagerObjectiveQuestion.adapter = mAssignmentPagerAdapter
                            mAssignmentPagerAdapter!!.notifyDataSetChanged()
                            binding.pagerObjectiveQuestion.setOnTouchListener { _, _ -> true }


                            binding.tvQuestionNo.text = (onPageChangeListener.currentPage + 1).toString().padStart(2, '0')

                            mRemainingCount = mAssignmentQuestionModel.size
                            binding.tvRemainingQues.text = mRemainingCount.toString()
                        }


                    } else {
                        UtilsFunctions().showToast(
                            this@AssignmentQuestionsActivity,
                            "No data found !!"
                        )
                        finish()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@AssignmentQuestionsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onObjectiveAnswerCallback(position: Int, answerId: Int) {
        mAssignmentQuestionModel[position].selectedAnswerId = answerId
        mStudentAssignmentQuestionsAdapter!!.notifyDataSetChanged()

        mRemainingCount = mAssignmentQuestionModel.count { it.selectedAnswerId == 0 }
        mSolvedCount = mAssignmentQuestionModel.count { it.selectedAnswerId != 0 }

        binding.tvSolvedQues.text = mSolvedCount.toString()
        binding.tvRemainingQues.text = mRemainingCount.toString()
    }

    override fun onSubjectiveAnswerCallback(position: Int, answer: String) {

        mAssignmentQuestionModel[position].selectedAnswer = answer
        mStudentAssignmentQuestionsAdapter!!.notifyDataSetChanged()

        mRemainingCount = mAssignmentQuestionModel.count { it.selectedAnswer.isNullOrEmpty() }
        mSolvedCount = mAssignmentQuestionModel.count { !it.selectedAnswer.isNullOrEmpty()}

        binding.tvSolvedQues.text = mSolvedCount.toString()
        binding.tvRemainingQues.text = mRemainingCount.toString()
    }

    private fun apiSubmitExam() {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@AssignmentQuestionsActivity)

        val jsonObject = JsonObject()


        val jsonArray = JsonArray()
        for (questionModel in mAssignmentQuestionModel) {
            if (questionModel.questionType == 1) {
                val questionObject = JsonObject()
                questionObject.addProperty("questionId", questionModel.questionId)
                questionObject.addProperty("questionType", questionModel.questionType)
                questionObject.addProperty("selectedAnswerId", questionModel.selectedAnswerId)
                jsonArray.add(questionObject)
            } else {
                val questionObject = JsonObject()
                questionObject.addProperty("questionId", questionModel.questionId)
                questionObject.addProperty("questionType", questionModel.questionType)
                questionObject.addProperty("answer", questionModel.selectedAnswer)
                jsonArray.add(questionObject)
            }
        }
        jsonObject.add("assignment_questions", jsonArray)


        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@AssignmentQuestionsActivity)
            .myApi.api_StudentSubmitAssignment(mExamId, jsonObject)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Assignment submit successfully") {
                            UtilsFunctions().showToast(this@AssignmentQuestionsActivity, message)
                            finish()
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@AssignmentQuestionsActivity, responseObject.getString("error")
                        )
                    }

                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@AssignmentQuestionsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }

}