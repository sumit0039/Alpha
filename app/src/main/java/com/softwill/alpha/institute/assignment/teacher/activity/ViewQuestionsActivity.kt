package com.softwill.alpha.institute.assignment.teacher.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityViewQuestionsBinding
import com.softwill.alpha.institute.assignment.teacher.Model.ViewQuestion
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewQuestionsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityViewQuestionsBinding
    private var mAssignmentId: Int? = 0;
    private var mOnlineExamId: Int? = 0;
    private var mForm: String? = null

    val mViewQuestion = ArrayList<ViewQuestion>()
    var mCurrentQuestion: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_view_questions
            )


        val bundle: Bundle? = intent.extras
        mAssignmentId = bundle?.getInt("mAssignmentId")
        mOnlineExamId = bundle?.getInt("mOnlineExamId")
        mForm = bundle?.getString("mForm")



        setupBack()
        apiViewAssignment()

        binding.btnPrevious.setOnClickListener {
            if (mCurrentQuestion > 1) {
                mCurrentQuestion -= 1
                updateQuestionViews()
            }
        }

        binding.btnNext.setOnClickListener {
            if (mCurrentQuestion < mViewQuestion.size) {
                mCurrentQuestion += 1
                updateQuestionViews()

            }
        }

    }


    private fun updateQuestionViews() {
        val currentQuestion = mViewQuestion[mCurrentQuestion - 1]
        binding.tvPos.text = UtilsFunctions().upTo2digits(mCurrentQuestion.toString())
        binding.etDescription.setText(currentQuestion.question)

        if (currentQuestion.questionType == 1) {
            binding.etOption1.setText(currentQuestion.options?.getOrNull(0)?.answer)
            binding.etOption2.setText(currentQuestion.options?.getOrNull(1)?.answer)
            binding.etOption3.setText(currentQuestion.options?.getOrNull(2)?.answer)
            binding.etOption4.setText(currentQuestion.options?.getOrNull(3)?.answer)


            binding.rbOption1.isChecked = currentQuestion.options?.get(0)?.isCorrect ?: false
            binding.rbOption2.isChecked = currentQuestion.options?.get(1)?.isCorrect ?: false
            binding.rbOption3.isChecked = currentQuestion.options?.get(2)?.isCorrect ?: false
            binding.rbOption4.isChecked = currentQuestion.options?.get(3)?.isCorrect ?: false

        }



        binding.radioGroup.visibility = if (currentQuestion.questionType == 1) View.VISIBLE else View.GONE
    }


    private fun apiViewAssignment() {

        val retrofit = RetrofitClient.getInstance(this@ViewQuestionsActivity).myApi
        val call: Call<ResponseBody> = when (mForm) {
            "Assignment" -> retrofit.api_ViewAssignment(mAssignmentId!!)
            else -> retrofit.api_ViewOnlineExam(mOnlineExamId!!)
        }

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType =
                            object : TypeToken<List<ViewQuestion>>() {}.type
                        val mList: List<ViewQuestion> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mViewQuestion.clear()
                        mViewQuestion.addAll(mList)

                        if (mViewQuestion.isNotEmpty()) {
                            mCurrentQuestion = 1
                            updateQuestionViews()
                        }


                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ViewQuestionsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_view_questions)
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
}