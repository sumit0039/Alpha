package com.softwill.alpha.institute.classes.activity

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
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityClassesBinding
import com.softwill.alpha.institute.classes.adapter.ClassSubjectAdapter
import com.softwill.alpha.institute.classes.adapter.StudentAdapter
import com.softwill.alpha.institute.classes.model.ClassInfo
import com.softwill.alpha.institute.classes.model.StudentInfo
import com.softwill.alpha.institute.classes.model.StudentSubject
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassesActivity : AppCompatActivity(), LectureClassAdapter.LectureClassCallbackInterface {

    private lateinit var binding: ActivityClassesBinding
    var yourPreference: YourPreference? = null
    var IsStudentLogin: Boolean = true
    var mStudentAdapter: StudentAdapter? = null
    var mClassSubjectAdapter: ClassSubjectAdapter? = null
    var mLectureClassAdapter: LectureClassAdapter? = null
    private val mStudentInfo = ArrayList<StudentInfo>()


    val mLectureClassModel = java.util.ArrayList<LectureClassModel>()
    val mStudentSubject = java.util.ArrayList<StudentSubject>()

    var mClassId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_classes)
        yourPreference = YourPreference(applicationContext)
        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()





        setupBack()
        onClickListener()
        setupAdapter()


        if (IsStudentLogin) {
            binding.llTop.visibility = View.VISIBLE
            binding.rvLectureClass.visibility = View.GONE
            apiClassStudentList()
        } else {
            apiClassSubjects()

            binding.rvLectureClass.visibility = View.VISIBLE
            binding.llTop.visibility = View.GONE
        }
    }

    private fun setupAdapter() {

        mLectureClassAdapter =
            LectureClassAdapter(this@ClassesActivity, mLectureClassModel, this)
        binding.rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

        mStudentAdapter = StudentAdapter(mStudentInfo, this@ClassesActivity, IsStudentLogin)
        binding.rvStudent.adapter = mStudentAdapter
        mStudentAdapter!!.notifyDataSetChanged()


        mClassSubjectAdapter = ClassSubjectAdapter(this@ClassesActivity, mStudentSubject)
        binding.rvSubjects.adapter = mClassSubjectAdapter
        mClassSubjectAdapter!!.notifyDataSetChanged()
    }




    private fun onClickListener() {
        binding.tvClass.setOnClickListener {
            binding.tvClass.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.tvClass.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvSubject.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            binding.tvSubject.setBackgroundResource(R.drawable.bg_rounded_3)


            binding.llClassView.visibility = View.VISIBLE
            binding.rvSubjects.visibility = View.GONE


        }

        binding.tvSubject.setOnClickListener {
            binding.tvSubject.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            binding.tvSubject.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvClass.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvClass.setBackgroundResource(R.drawable.bg_rounded_3)


            binding.llClassView.visibility = View.GONE
            binding.rvSubjects.visibility = View.VISIBLE

            apiClassSubjectList()
        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_class)
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

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className: String) {
        mClassId = classId
        apiClassStudentList()
    }


    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ClassesActivity).myApi.api_ClassSubjects()

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
                            apiClassStudentList()
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ClassesActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiClassStudentList() {

        val call: Call<ResponseBody> = if (!IsStudentLogin) {
            RetrofitClient.getInstance(this@ClassesActivity).myApi.api_ClassStudentListByTeacher(mClassId)
        } else {
            RetrofitClient.getInstance(this@ClassesActivity).myApi.api_ClassStudentListByStudent()
        }


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val classInfo = Gson().fromJson(responseBody, ClassInfo::class.java)

                        binding.tvClassTeacherName.text = classInfo.classTeacher
                        binding.tvStreamName.text = classInfo.streamName


                        if (!classInfo.students.isNullOrEmpty()) {
                            binding.rvStudent.visibility = View.VISIBLE
                            mStudentAdapter?.updateData(classInfo.students)
                        } else {
                            binding.rvStudent.visibility = View.GONE

                        }
                    } else {
                        binding.rvStudent.visibility = View.GONE
                    }

                } else {
                    binding.rvStudent.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@ClassesActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiClassSubjectList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ClassesActivity).myApi.api_ClassSubjectList()


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val listType = object : TypeToken<List<StudentSubject>>() {}.type
                        val mList: List<StudentSubject> = Gson().fromJson(responseBody, listType)

                        mStudentSubject.clear()
                        mStudentSubject.addAll(mList)


                        if (!mStudentSubject.isNullOrEmpty()) {
                            binding.rvSubjects.visibility = View.VISIBLE
                            mClassSubjectAdapter?.notifyDataSetChanged()
                        } else {
                            binding.rvSubjects.visibility = View.GONE

                        }
                    } else {
                        binding.rvSubjects.visibility = View.GONE
                    }

                } else {
                    binding.rvSubjects.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@ClassesActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



}