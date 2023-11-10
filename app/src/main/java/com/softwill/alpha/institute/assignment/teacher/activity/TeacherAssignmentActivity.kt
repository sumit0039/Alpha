package com.softwill.alpha.institute.assignment.teacher.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.adapter.LectureClassAdapter.LectureClassCallbackInterface
import com.softwill.alpha.common.adapter.LectureSubjectAdapterNew
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityTeacherAssignmentBinding
import com.softwill.alpha.institute.assignment.teacher.Model.CreateAssignment
import com.softwill.alpha.institute.assignment.teacher.Model.TeacherCompletedAssignment
import com.softwill.alpha.institute.assignment.teacher.Model.TeacherOngoingAssignment
import com.softwill.alpha.institute.assignment.teacher.adapter.TAssignmentCompletedAdapter
import com.softwill.alpha.institute.assignment.teacher.adapter.TAssignmentOngoingAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TeacherAssignmentActivity : AppCompatActivity(), LectureClassCallbackInterface,
    LectureSubjectAdapterNew.LectureSubjectCallbackInterface,
    TAssignmentOngoingAdapter.TAssignmentOngoingAdapterCallbackInterface {

    private lateinit var binding: ActivityTeacherAssignmentBinding
    var mTAssignmentOngoingAdapter: TAssignmentOngoingAdapter? = null
    var mTAssignmentCompletedAdapter: TAssignmentCompletedAdapter? = null
    var mLectureClassAdapter: LectureClassAdapter? = null
    var mLectureSubjectAdapter: LectureSubjectAdapterNew? = null

    private var rvLectureSubject: RecyclerView? = null
    val mLectureClassModel = ArrayList<LectureClassModel>()

    val mTeacherOngoingAssignment = ArrayList<TeacherOngoingAssignment>()
    val mTeacherCompletedAssignment = ArrayList<TeacherCompletedAssignment>()

    var mClassId: Int = -1
    var mSubjectId: Int = -1

    var isCompletedOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, com.softwill.alpha.R.layout.activity_teacher_assignment
        )


        setupBack()
        onClickListener()
        setupAdapter()

        //API CALL
        apiTeacherOngoingAssignment()
        apiTeacherCompletedAssignment("", "", false)
    }


    private fun setupAdapter() {

        mTAssignmentOngoingAdapter =
            TAssignmentOngoingAdapter(this, mTeacherOngoingAssignment, this)
        binding.rvTOngoingAssignment.adapter = mTAssignmentOngoingAdapter
        mTAssignmentOngoingAdapter!!.notifyDataSetChanged()


        mTAssignmentCompletedAdapter =
            TAssignmentCompletedAdapter(this, mTeacherCompletedAssignment)
        binding.rvTCompletedAssignment.adapter = mTAssignmentCompletedAdapter
        mTAssignmentCompletedAdapter!!.notifyDataSetChanged()
    }


    @SuppressLint("SetTextI18n")
    private fun addAssignmentBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_assignment, null)

        var radioGroup: RadioGroup? = null
        var radioButton: RadioButton? = null
        val btnStartCreating = view.findViewById<Button>(R.id.btnStartCreating)
        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)
        radioGroup = view.findViewById(R.id.radioGroup)
        val rbObjective = view.findViewById<RadioButton>(R.id.rbObjective)
        val rbSubjective = view.findViewById<RadioButton>(R.id.rbSubjective)
        val etStartDate = view.findViewById<EditText>(R.id.etStartDate)
        val etEndDate = view.findViewById<EditText>(R.id.etEndDate)
        val etTotalMarks = view.findViewById<EditText>(R.id.etTotalMarks)

        mLectureClassAdapter =
            LectureClassAdapter(this@TeacherAssignmentActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

        apiClassSubjects()

        etStartDate.setOnClickListener {
            UtilsFunctions().showDatePicker(this, etStartDate, false)
        }

        etEndDate.setOnClickListener {
            UtilsFunctions().showDatePicker(this, etEndDate, false)
        }


        btnStartCreating.setOnClickListener {
            val id: Int = radioGroup!!.checkedRadioButtonId
            if (id != -1) {
                radioButton = findViewById(id)

                var mType = "Objective"
                if (rbSubjective.isChecked) {
                    mType = "Subjective"
                }

                if (etStartDate.text.toString().isEmpty()) {
                    UtilsFunctions().showToast(this, "Select start date")
                } else if (etEndDate.text.toString().isEmpty()) {
                    UtilsFunctions().showToast(this, "Select end date")
                } else if (etTotalMarks.text.toString().isEmpty()) {
                    UtilsFunctions().showToast(this, "Enter total marks")
                } else if ((etTotalMarks.text.toString()).toInt() == 0) {
                    UtilsFunctions().showToast(this, "Total marks can't be zero")
                } else {

                    val createAssignment = CreateAssignment(
                        examType = if (mType == "Objective") 1 else 2,
                        classId = mClassId,
                        subjectId = mSubjectId,
                        startDate = etStartDate.text.toString(),
                        endDate = etEndDate.text.toString(),
                        totalMarks = etTotalMarks.text.toString().toInt()
                    )


                    val intent = Intent(this, CreateAssignmentActivity::class.java)
                    intent.putExtra("mCreateAssignment", createAssignment)
                    startActivity(intent)
                    dialog.dismiss()

                }


            } else {
                UtilsFunctions().showToast(this, "Nothing selected")
            }
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }


    private fun addFilterBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_filter_teacher_assignment, null)


        val rvLectureClass = view.findViewById<RecyclerView>(R.id.rvLectureClass)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)
        val btnReset = view.findViewById<Button>(R.id.btnReset)
        val btnApply = view.findViewById<Button>(R.id.btnApply)

        mLectureClassAdapter =
            LectureClassAdapter(this@TeacherAssignmentActivity, mLectureClassModel, this)
        rvLectureClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

        apiClassSubjects()


        btnApply.setOnClickListener {
            dialog.dismiss()
            apiTeacherCompletedAssignment(mClassId.toString(), mSubjectId.toString(), true)
        }

        btnReset.setOnClickListener {
            dialog.dismiss()
            apiTeacherCompletedAssignment("", "", true)
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun onClickListener() {
        binding.fab.setOnClickListener {
            addAssignmentBottomSheet()
        }

        binding.clFilter.setOnClickListener {
            addFilterBottomSheet()
        }


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


            binding.rvTOngoingAssignment.visibility = View.VISIBLE
            binding.rvTCompletedAssignment.visibility = View.GONE

            binding.clFilter.visibility = View.GONE

            isCompletedOn = false
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


            binding.rvTOngoingAssignment.visibility = View.GONE
            binding.rvTCompletedAssignment.visibility = View.VISIBLE


            binding.clFilter.visibility = View.VISIBLE

            isCompletedOn = true
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


    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherAssignmentActivity).myApi.api_ClassSubjects()

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
                    UtilsFunctions().handleErrorResponse(response, this@TeacherAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className :String) {
        mClassId = classId
        setSubjectAdapter(position, subjectId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSubjectAdapter(position: Int, subjectId: Int) {
        mSubjectId = subjectId
        mLectureSubjectAdapter = LectureSubjectAdapterNew(
            this@TeacherAssignmentActivity, mLectureClassModel[position].class_subjects, this
        )
        rvLectureSubject?.adapter = mLectureSubjectAdapter
        mLectureSubjectAdapter?.notifyDataSetChanged()
    }

    override fun lectureSubjectClickCallback(subjectId: Int, position: Int) {
        mSubjectId = subjectId

    }

    override fun onResume() {
        super.onResume()
        if (isCompletedOn) {
            apiTeacherCompletedAssignment("", "", true)
        } else {
            apiTeacherOngoingAssignment()
        }

    }

    private fun apiTeacherOngoingAssignment() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherAssignmentActivity).myApi.api_TeacherOngoingAssignment()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<TeacherOngoingAssignment>>() {}.type
                        val mList: List<TeacherOngoingAssignment> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mTeacherOngoingAssignment.clear()
                        mTeacherOngoingAssignment.addAll(mList)


                        if (mTeacherOngoingAssignment.isNotEmpty()) {
                            mTAssignmentOngoingAdapter?.notifyDataSetChanged()
                            binding.rvTOngoingAssignment.visibility = View.VISIBLE
                        } else {
                            binding.rvTOngoingAssignment.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvTOngoingAssignment.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@TeacherAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiTeacherCompletedAssignment(
        classId: String,
        subjectId: String,
        isResumed: Boolean
    ) {
        val call: Call<ResponseBody> =

            RetrofitClient.getInstance(this@TeacherAssignmentActivity).myApi.api_TeacherCompletedAssignment(
                classId,
                subjectId
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType =
                            object : TypeToken<List<TeacherCompletedAssignment>>() {}.type
                        val mList: List<TeacherCompletedAssignment> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mTeacherCompletedAssignment.clear()
                        mTeacherCompletedAssignment.addAll(mList)


                        if (mTeacherCompletedAssignment.isNotEmpty()) {
                            mTAssignmentCompletedAdapter?.notifyDataSetChanged()
                            if (isResumed) {
                                binding.rvTCompletedAssignment.visibility = View.VISIBLE
                            } else {
                                binding.rvTCompletedAssignment.visibility = View.GONE
                            }

                        } else {
                            binding.rvTCompletedAssignment.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvTCompletedAssignment.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@TeacherAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun deleteAssignmentCallback(position: Int, id: Int) {
        apiDeleteTeacherAssignment(id, position)
    }


    private fun apiDeleteTeacherAssignment(id: Int, position: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeacherAssignmentActivity).myApi.api_DeleteTeacherAssignment(
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
                            mTAssignmentOngoingAdapter?.removeItem(position)
                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@TeacherAssignmentActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@TeacherAssignmentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}