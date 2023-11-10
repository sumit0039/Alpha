package com.softwill.alpha.institute.online_exam.teacher.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.PreviousQuestionsAdapter
import com.softwill.alpha.databinding.ActivityCreateExamBinding
import com.softwill.alpha.institute.assignment.teacher.Model.CreateOption
import com.softwill.alpha.institute.assignment.teacher.Model.CreateQuestion
import com.softwill.alpha.institute.library.activity.SavedActivity
import com.softwill.alpha.institute.online_exam.teacher.model.CreateOnlineExam
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateExamBinding
    //var mTeacherExamQuestionsAdapter: TeacherExamQuestionsAdapter? = null

    var mPreviousQuestionsAdapter: PreviousQuestionsAdapter? = null


    private var mCreateOnlineExam: CreateOnlineExam? = null
    private var mQuestionsList = ArrayList<CreateQuestion>()


    private var currentIndex: Int = 0

    var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_create_exam
            )


        mCreateOnlineExam = intent.getSerializableExtra("mCreateOnlineExam") as CreateOnlineExam


        setupBack()
        onClickListener()
        setupAdapter()


        binding.tvQuesNo.text = UtilsFunctions().upTo2digits((currentIndex + 1).toString())
    }

    private fun setupAdapter() {

        mPreviousQuestionsAdapter = PreviousQuestionsAdapter(applicationContext, mQuestionsList)
        binding.rvQuestions.adapter = mPreviousQuestionsAdapter
        mPreviousQuestionsAdapter!!.notifyDataSetChanged()


    }


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
            if (mQuestionsList.isNotEmpty()) {
                addConfirmationBottomSheet()
            } else {
                UtilsFunctions().showToast(this, "Add some question")
            }
        }

        // Manually manage the selection of RadioButtons
        binding.rbOption1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rbOption2.isChecked = false
                binding.rbOption3.isChecked = false
                binding.rbOption4.isChecked = false
            }
        }

        binding.rbOption2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rbOption1.isChecked = false
                binding.rbOption3.isChecked = false
                binding.rbOption4.isChecked = false
            }
        }

        binding.rbOption3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rbOption1.isChecked = false
                binding.rbOption2.isChecked = false
                binding.rbOption4.isChecked = false
            }
        }

        binding.rbOption4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rbOption1.isChecked = false
                binding.rbOption2.isChecked = false
                binding.rbOption3.isChecked = false
            }
        }

        binding.btnNext.setOnClickListener {


            var question = binding.etQuestion.text.toString().trim()

            var option1 = binding.etOption1.text.toString().trim()
            var option2 = binding.etOption2.text.toString().trim()
            var option3 = binding.etOption3.text.toString().trim()
            var option4 = binding.etOption4.text.toString().trim()



            if (question.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter question")
            } else if (option1.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter option 1")
            } else if (option2.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter option 2")
            } else if (option3.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter option 3")
            } else if (option4.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter option 4")
            } else {
                val optionsList = listOf(
                    CreateOption(option1, binding.rbOption1.isChecked),
                    CreateOption(option2, binding.rbOption2.isChecked),
                    CreateOption(option3, binding.rbOption3.isChecked),
                    CreateOption(option4, binding.rbOption4.isChecked)
                )


                if (currentIndex >= 0 && currentIndex < mQuestionsList.size) {
                    // Update the existing question
                    mQuestionsList[currentIndex] = CreateQuestion(question, optionsList)
                } else {
                    // Add a new question
                    mQuestionsList.add(CreateQuestion(question, optionsList))
                }

                currentIndex += 1
                binding.tvQuesNo.text =
                    UtilsFunctions().upTo2digits((currentIndex + 1).toString())
                if (currentIndex >= 0 && currentIndex < mQuestionsList.size) {
                    binding.etQuestion.setText(mQuestionsList[currentIndex].question)

                    binding.etOption1.setText(mQuestionsList[currentIndex].options?.get(0)?.option)
                    binding.etOption2.setText(mQuestionsList[currentIndex].options?.get(1)?.option)
                    binding.etOption3.setText(mQuestionsList[currentIndex].options?.get(2)?.option)
                    binding.etOption4.setText(mQuestionsList[currentIndex].options?.get(3)?.option)

                    binding.rbOption1.isChecked =
                        mQuestionsList[currentIndex].options?.get(0)?.isCorrect == true
                    binding.rbOption2.isChecked =
                        mQuestionsList[currentIndex].options?.get(1)?.isCorrect == true
                    binding.rbOption3.isChecked =
                        mQuestionsList[currentIndex].options?.get(2)?.isCorrect == true
                    binding.rbOption4.isChecked =
                        mQuestionsList[currentIndex].options?.get(3)?.isCorrect == true

                } else {
                    binding.rbOption1.isChecked = true
                    binding.etQuestion.text.clear()
                    binding.etOption1.text.clear()
                    binding.etOption2.text.clear()
                    binding.etOption3.text.clear()
                    binding.etOption4.text.clear()
                }

                mPreviousQuestionsAdapter?.notifyDataSetChanged()

            }

            print(mQuestionsList)

        }

        binding.btnPrevious.setOnClickListener {
            if (currentIndex > 0) {

                currentIndex -= 1
                binding.tvQuesNo.text =
                    UtilsFunctions().upTo2digits((currentIndex + 1).toString())
                binding.etQuestion.setText(mQuestionsList[currentIndex].question)

                binding.etOption1.setText(mQuestionsList[currentIndex].options?.get(0)?.option)
                binding.etOption2.setText(mQuestionsList[currentIndex].options?.get(1)?.option)
                binding.etOption3.setText(mQuestionsList[currentIndex].options?.get(2)?.option)
                binding.etOption4.setText(mQuestionsList[currentIndex].options?.get(3)?.option)


                binding.rbOption1.isChecked =
                    mQuestionsList[currentIndex].options?.get(0)?.isCorrect == true
                binding.rbOption2.isChecked =
                    mQuestionsList[currentIndex].options?.get(1)?.isCorrect == true
                binding.rbOption3.isChecked =
                    mQuestionsList[currentIndex].options?.get(2)?.isCorrect == true
                binding.rbOption4.isChecked =
                    mQuestionsList[currentIndex].options?.get(3)?.isCorrect == true

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
            apiTeacherCreateExam()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun apiTeacherCreateExam() {

        progressDialog = UtilsFunctions().showCustomProgressDialog(this@CreateExamActivity)

        val questionsArray = JsonArray()

        for (questionModel in mQuestionsList) {
            val questionObject = JsonObject().apply {
                addProperty("question", questionModel.question)


                val optionsArray = JsonArray().apply {
                    for (option in questionModel.options ?: emptyList()) {
                        val optionObject = JsonObject().apply {
                            addProperty("option", option.option)
                            addProperty("isCorrect", option.isCorrect)
                        }
                        add(optionObject)
                    }
                }
                add("options", optionsArray)
            }
            questionsArray.add(questionObject)
        }

        val assignmentJson = JsonObject().apply {
            addProperty("examTypeId", mCreateOnlineExam?.examTypeId ?: 0)
            addProperty("classId", mCreateOnlineExam?.classId ?: 0)
            addProperty("subjectId", mCreateOnlineExam?.subjectId ?: 0)
            addProperty("examDate", mCreateOnlineExam?.examDate ?: "")
            addProperty("startTime", mCreateOnlineExam?.startTime ?: "")
            addProperty("endTime", mCreateOnlineExam?.endTime ?: "")
            addProperty("totalMarks", mCreateOnlineExam?.totalMarks ?: 0)
            add("questions", questionsArray)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_TeacherCreateExam(assignmentJson)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Created successfully") {
                            finish()
                        }

                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@CreateExamActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@CreateExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_create_exam)
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
}