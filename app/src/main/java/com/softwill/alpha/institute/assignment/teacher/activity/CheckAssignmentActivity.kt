package com.softwill.alpha.institute.assignment.teacher.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityCheckAssignmentBinding
import com.softwill.alpha.institute.assignment.teacher.Model.CheckAssignment
import com.softwill.alpha.institute.assignment.teacher.Model.QuestionAnswer
import com.softwill.alpha.institute.assignment.teacher.adapter.CheckQuestionsAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckAssignmentActivity : AppCompatActivity(), CheckQuestionsAdapter.CheckQuestionsAdapterCallbackInterface {

    private lateinit var binding: ActivityCheckAssignmentBinding
    var mCheckQuestionsAdapter: CheckQuestionsAdapter? = null
    private val mQuestionAnswer = ArrayList<QuestionAnswer>()
    private var mExamId: Int = 0
    private var mTotalMarks: Int = 0

    var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_check_assignment
            )

        val bundle: Bundle? = intent.extras
        mExamId = bundle!!.getInt("mExamId")

        setupBack()
        setupCheckAdapter()
        apiTeacherCheckAssignment()


        binding.btnSubmit.setOnClickListener {
            var isAnswerAll: Boolean = true
            for (item in mQuestionAnswer) {
                if (item.isCorrect == null) {
                    isAnswerAll = false
                }
            }

            if (isAnswerAll) {
                apiTeacherSubmitCheckAssignment()
            } else {
                UtilsFunctions().showToast(this@CheckAssignmentActivity, "Please check all questions")
            }


        }

    }

    private fun setupCheckAdapter() {

        mCheckQuestionsAdapter = CheckQuestionsAdapter(this, mQuestionAnswer, this@CheckAssignmentActivity)
        binding.rvCheckQuestions.adapter = mCheckQuestionsAdapter
        mCheckQuestionsAdapter!!.notifyDataSetChanged()


    }

    private fun apiTeacherCheckAssignment() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CheckAssignmentActivity).myApi.api_TeacherCheckAssignment(mExamId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val checkAssignment = Gson().fromJson(responseBody, CheckAssignment::class.java)

                        binding.tvStudentName.text = checkAssignment.studentName
                        binding.tvSubjectName.text = checkAssignment.subjectName

                        mTotalMarks = checkAssignment.totalMarks

                        if (!checkAssignment.question_answers.isNullOrEmpty()) {
                            binding.rvCheckQuestions.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mQuestionAnswer.clear()
                            mQuestionAnswer.addAll(checkAssignment.question_answers)
                            mCheckQuestionsAdapter!!.notifyDataSetChanged()

                        } else {
                            binding.rvCheckQuestions.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@CheckAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_check)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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

    override fun correctAnswerCallback(id: Int, position: Int) {
        var mRemaining = mTotalMarks


        for (item in mQuestionAnswer) {
            if (item.isCorrect == true) {
                mRemaining -= item.givenMark!!
            }
        }


        if (mQuestionAnswer[position].isCorrect == true) {
            giveMarksBottomSheet(position, mQuestionAnswer[position].givenMark!! + mRemaining)
        } else {
            if (mRemaining > 0) {
                giveMarksBottomSheet(position, mRemaining)
            } else {
                UtilsFunctions().showToast(this@CheckAssignmentActivity, "Already reached maximum marks")
            }
        }

    }


    override fun wrongAnswerCallback(id: Int, position: Int) {
        mQuestionAnswer[position].isCorrect = false

        mCheckQuestionsAdapter!!.notifyDataSetChanged()
    }

    private fun giveMarksBottomSheet(position: Int, mRemaining: Int) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_give_marks, null)


        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvDone = view.findViewById<TextView>(R.id.tvDone)
        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)
        var marks = 0

        numberPicker.minValue = 1
        numberPicker.maxValue = mRemaining
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            marks = newVal
        }

        tvDone.setOnClickListener {
            dialog.dismiss()
            mQuestionAnswer[position].isCorrect = true
            mQuestionAnswer[position].givenMark = marks

            mCheckQuestionsAdapter!!.notifyDataSetChanged()
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun apiTeacherSubmitCheckAssignment() {

        progressDialog = UtilsFunctions().showCustomProgressDialog(this@CheckAssignmentActivity)

        val questionsArray = JsonArray()

        for (questionAnswer in mQuestionAnswer) {
            val questionObject = JsonObject().apply {
                addProperty("id", questionAnswer.id)
                addProperty("isCorrect", questionAnswer.isCorrect)
                if (questionAnswer.isCorrect == true) {
                    addProperty("givenMark", questionAnswer.givenMark)
                }
            }
            questionsArray.add(questionObject)
        }

        val assignmentJson = JsonObject().apply {
            add("assignment_questions", questionsArray)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_TeacherSubmitCheckAssignment(mExamId, assignmentJson)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Assignment submit successfully") {
                            finish()
                        }

                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@CheckAssignmentActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@CheckAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }


}