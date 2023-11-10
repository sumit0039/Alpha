package com.softwill.alpha.institute.lecture.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.adapter.LectureSubjectAdapterNew
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityLectureBinding
import com.softwill.alpha.institute.lecture.adapter.LectureAdapter
import com.softwill.alpha.institute.lecture.model.LectureTeacher
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LectureActivity : AppCompatActivity(),
    LectureClassAdapter.LectureClassCallbackInterface,
    LectureSubjectAdapterNew.LectureSubjectCallbackInterface,
    LectureAdapter.LectureAdapterCallbackInterface {

    private lateinit var binding: ActivityLectureBinding
    var yourPreference: YourPreference? = null
    var IsStudentLogin: Boolean = true

    var mLectureAdapter: LectureAdapter? = null
    var mLectureClassAdapter: LectureClassAdapter? = null
    var mLectureSubjectAdapter: LectureSubjectAdapterNew? = null

    private var rvLectureSubject: RecyclerView? = null
    val mLectureClassModel = ArrayList<LectureClassModel>()

    val mLectureTeacher = java.util.ArrayList<LectureTeacher>()

    var mClassId: Int = -1
    var mSubjectId: Int = -1

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lecture)
        yourPreference = YourPreference(applicationContext)
        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()


        if (IsStudentLogin) {
            binding.fab.visibility = View.GONE
        } else {
            binding.fab.visibility = View.VISIBLE
        }



        setupBack()
        onClickListener()
        setupTeacherLecture()

        apiLecture("today")

    }


    private fun setupTeacherLecture() {

        mLectureAdapter = LectureAdapter(mLectureTeacher, this, IsStudentLogin, true, this)
        binding.rvLecture.adapter = mLectureAdapter
        mLectureAdapter!!.notifyDataSetChanged()
    }


    private fun onClickListener() {
        binding.tvTodays.setOnClickListener {
            binding.tvTodays.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.tvTodays.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvUpcoming.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvUpcoming.setBackgroundResource(R.drawable.bg_rounded_3)


            apiLecture("today")

        }

        binding.tvUpcoming.setOnClickListener {
            binding.tvUpcoming.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            binding.tvUpcoming.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvTodays.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvTodays.setBackgroundResource(R.drawable.bg_rounded_3)


            apiLecture("upcoming")

        }


        binding.fab.setOnClickListener {
            bottomSheetAddLecture()
        }
    }


    private fun bottomSheetAddLecture() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_lecture, null)


        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)

        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etStartTime = view.findViewById<EditText>(R.id.etStartTime)
        val etEndTime = view.findViewById<EditText>(R.id.etEndTime)

        mLectureClassAdapter = LectureClassAdapter(this@LectureActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

        apiClassSubjects()

        etDate.setOnClickListener {
            UtilsFunctions().showDatePicker(this, etDate, true)
        }


        etStartTime.setOnClickListener {
            UtilsFunctions().showTimePicker(this, etStartTime)
        }

        etEndTime.setOnClickListener {
            if (!etStartTime.text.toString().isEmpty()) {

                UtilsFunctions().showEndTimePicker(this, etEndTime, etStartTime.text.toString())
            } else {
                UtilsFunctions().showToast(this@LectureActivity, "Please select start time first")
            }
        }




        btnSave.setOnClickListener {

            if (etDate.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Select date")
            } else if (etStartTime.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Select start time")
            } else if (etEndTime.text.toString().isEmpty()) {
                UtilsFunctions().showToast(this, "Enter end time")
            } else {
                apiTeacherCreateLecture(
                    etDate.text.toString().trim(),
                    etStartTime.text.toString().trim(),
                    etEndTime.text.toString().trim()
                )
                dialog.dismiss()

            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_lecture)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true)


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

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className :String) {
        mClassId = classId
        setSubjectAdapter(position, subjectId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSubjectAdapter(position: Int, subjectId: Int) {
        mSubjectId = subjectId
        mLectureSubjectAdapter = LectureSubjectAdapterNew(
            this@LectureActivity, mLectureClassModel[position].class_subjects, this
        )
        rvLectureSubject?.adapter = mLectureSubjectAdapter
        mLectureSubjectAdapter?.notifyDataSetChanged()
    }

    override fun lectureSubjectClickCallback(subjectId: Int, position: Int) {
        mSubjectId = subjectId
    }


    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LectureActivity).myApi.api_ClassSubjects()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<LectureClassModel>>() {}.type
                        val mList: List<LectureClassModel> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mLectureClassModel.clear()
                        mLectureClassModel.addAll(mList)


                        if (mLectureClassModel.isNotEmpty()) {
                            mClassId = mLectureClassModel[0].classId
                            mLectureClassAdapter?.notifyDataSetChanged()

                            if (mLectureClassModel[0].class_subjects.isNotEmpty()) {
                                setSubjectAdapter(
                                    0, mLectureClassModel[0].class_subjects[0].subjectId
                                )

                            }

                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@LectureActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiLecture(type: String) {

        val call: Call<ResponseBody> = if (IsStudentLogin) {
            RetrofitClient.getInstance(this@LectureActivity).myApi.api_StudentLecture(type)
        } else {
            RetrofitClient.getInstance(this@LectureActivity).myApi.api_TeacherLecture(type)
        }

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<LectureTeacher>>() {}.type
                        val mList: List<LectureTeacher> = Gson().fromJson(responseBody, listType)

                        // Update the mLectureTeacher list with the new data
                        mLectureTeacher.clear()
                        mLectureTeacher.addAll(mList)


                        if (mLectureTeacher.isNotEmpty()) {
                            val isToday: Boolean = type == "today"

                            mLectureAdapter = LectureAdapter(
                                mLectureTeacher,
                                this@LectureActivity,
                                IsStudentLogin,
                                isToday, this@LectureActivity
                            )
                            binding.rvLecture.adapter = mLectureAdapter
                            mLectureAdapter?.notifyDataSetChanged()
                            binding.rvLecture.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvLecture.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvLecture.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@LectureActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiTeacherCreateLecture(date: String, startTime: String, endTime: String) {

        progressDialog = UtilsFunctions().showCustomProgressDialog(this@LectureActivity)

        val LectureJson = JsonObject().apply {
            addProperty("classId", mClassId)
            addProperty("subjectId", mSubjectId)
            addProperty("lectureDate", date)
            addProperty("lectureStartTime", startTime)
            addProperty("lectureEndTime", endTime)
            addProperty("desc", "")
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_TeacherCreateLecture(LectureJson)

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

                            binding.tvTodays.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.white
                                )
                            )
                            binding.tvTodays.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                            binding.tvUpcoming.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.black
                                )
                            )
                            binding.tvUpcoming.setBackgroundResource(R.drawable.bg_rounded_3)


                            apiLecture("today")


                        }

                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@LectureActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@LectureActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }


    override fun deleteLectureCallback(position: Int, id: Int) {
        apiDeleteLecture(id, position)
    }

    private fun apiDeleteLecture(id: Int, position: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LectureActivity).myApi.api_DeleteLecture(
                id
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Deleted successfully") {
                            mLectureAdapter?.removeItem(position)
                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@LectureActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@LectureActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}