package com.softwill.alpha.institute.timetable.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import com.softwill.alpha.databinding.ActivityTimeTableBinding
import com.softwill.alpha.institute.timetable.adapter.TimeTableAdapter
import com.softwill.alpha.institute.timetable.model.TimeTableModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeTableActivity : AppCompatActivity(),
    TimeTableAdapter.TimeTableAdapterCallbackInterface,
    LectureClassAdapter.LectureClassCallbackInterface,
    LectureSubjectAdapterNew.LectureSubjectCallbackInterface {

    private lateinit var binding: ActivityTimeTableBinding
    var yourPreference: YourPreference? = null
    var IsStudentLogin: Boolean = true
    var mTimeTableAdapter: TimeTableAdapter? = null
    var mLectureClassAdapter: LectureClassAdapter? = null
    var mLectureSubjectAdapter: LectureSubjectAdapterNew? = null

    val mLectureClassModel = ArrayList<LectureClassModel>()
    private var rvLectureSubject: RecyclerView? = null


    var currentMonth: Int = 0
    var mClassId: Int = -1
    private var mSubjectId: Int = -1

    private var mTimeTimeMode: Int = 0
    private var mSelectedClassPos: Int = -1
    var progressDialog: Dialog? = null
    private val mTimeTableModel = ArrayList<TimeTableModel>()
    var currentDate: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_time_table
        )
        yourPreference = YourPreference(applicationContext)
        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()

        setupBack()
        setupAdapter()
        setupCalenderWeek()
        getCurrentMonth()
        onClickListener()
        setSpinnerMonth()



        if (IsStudentLogin) {
            binding.fab.visibility = View.GONE
        } else {
            setupClassAdapter()
            apiClassSubjects()
            getCurrentSelectedDate()
            apiTeacherTimeTableList()
            binding.fab.visibility = View.VISIBLE
        }


    }

    private fun getCurrentMonth() {
        val dateFormat: DateFormat = SimpleDateFormat("MM")
        val date = Date()
        currentMonth = dateFormat.format(date).toInt()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickListener() {
        binding.fab.setOnClickListener {
            bottomSheetAddTimeTable()
        }


        binding.tvToday.setOnClickListener {
            getCurrentSelectedDate()
            setupCalenderWeek();
        }


    }


    private fun bottomSheetAddTimeTable() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_timetable, null)


        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val etSetDate = view.findViewById<EditText>(R.id.etSetDate)
        val etSetDays = view.findViewById<EditText>(R.id.etSetDays)
        val etEndTime = view.findViewById<EditText>(R.id.etEndTime)
        val etStartTime = view.findViewById<EditText>(R.id.etStartTime)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)
        rvLectureSubject = view.findViewById(R.id.rvLectureSubject)

        setSubjectAdapter(mSelectedClassPos, mLectureClassModel[mSelectedClassPos].class_subjects[0].subjectId)


        etSetDays.setOnClickListener {
            etSetDays.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            etSetDays.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            etSetDate.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            etSetDate.setBackgroundResource(R.drawable.bg_rounded_3)
            etSetDate.setText(resources.getString(R.string.set_date))
            showCheckboxDropdownDialog(etSetDays, etSetDays)

            mTimeTimeMode = 2
        }

        etSetDate.setOnClickListener {
            etSetDate.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            etSetDate.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            etSetDays.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
            etSetDays.setBackgroundResource(R.drawable.bg_rounded_3)
            UtilsFunctions().showDatePicker(this, etSetDate, true)

            etSetDays.setText(resources.getString(R.string.set_days))
            mTimeTimeMode = 1

        }


        etStartTime.setOnClickListener {
            UtilsFunctions().showTimePicker(this, etStartTime)
        }

        etEndTime.setOnClickListener {
            if (!etStartTime.text.toString().isEmpty()) {
                UtilsFunctions().showEndTimePicker(this, etEndTime, etStartTime.text.toString())
            } else {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select start time first")
            }
        }


        btnSave.setOnClickListener {
            if (mTimeTimeMode == 0) {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select date or day")
            } else if (mTimeTimeMode == 1 || etSetDate.text.toString().trim().isEmpty()) {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select date")
            } else if (mTimeTimeMode == 2 || etSetDays.text.toString().trim().isEmpty()) {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select days")
            } else if (etStartTime.text.toString().trim().isEmpty()) {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select start time")
            } else if (etEndTime.text.toString().trim().isEmpty()) {
                UtilsFunctions().showToast(this@TimeTableActivity, "Please select end time")
            } else {
                dialog.dismiss()
              /*  if (mTimeTimeMode == 1) {
                    apiTeacherTimetableCreate(
                        etStartTime.text.toString().trim(),
                        etEndTime.text.toString().trim(),
                        etDesc.text.toString().trim(),
                        etSetDate.text.toString().trim()
                    )
                } else {
                    apiTeacherTimetableCreate(
                        etStartTime.text.toString().trim(),
                        etEndTime.text.toString().trim(),
                        etDesc.text.toString().trim(),
                        etSetDays.text.toString().trim()
                    )
                }*/

            }




        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()



    }


    private fun setupClassAdapter() {
        if (!IsStudentLogin) {
            mLectureClassAdapter = LectureClassAdapter(this@TimeTableActivity, mLectureClassModel, this)
            binding.rvLectureClass.adapter = mLectureClassAdapter
            mLectureClassAdapter?.notifyDataSetChanged()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalenderWeek() {
        val format: DateFormat = SimpleDateFormat("dd")
        val formatter1 = SimpleDateFormat("yyyy-MM-dd")
        val calendar: Calendar = Calendar.getInstance()
        val calendar2: Calendar = Calendar.getInstance()


        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = format.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)

        }

        calendar2.firstDayOfWeek = Calendar.MONDAY
        calendar2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days2 = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days2[i] = formatter1.format(calendar2.time)
            calendar2.add(Calendar.DAY_OF_MONTH, 1)
        }

        binding.tv1.text = days[0].toString()
        binding.tv2.text = days[1].toString()
        binding.tv3.text = days[2].toString()
        binding.tv4.text = days[3].toString()
        binding.tv5.text = days[4].toString()
        binding.tv6.text = days[5].toString()
        binding.tv7.text = days[6].toString()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd")
        val formatted = current.format(formatter)

        unselectAllTextViews(listOf(binding.tv1, binding.tv2, binding.tv3, binding.tv4, binding.tv5, binding.tv6, binding.tv7))

        val dayTextViews = arrayOf(
            binding.tv1, binding.tv2, binding.tv3,
            binding.tv4, binding.tv5, binding.tv6, binding.tv7
        )

        for ((index, dayTextView) in dayTextViews.withIndex()) {
            if (formatted == days[index]) {
                dayTextView.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                dayTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.primary_color)
            } else {
                dayTextView.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey_light2))
                dayTextView.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.white)
            }
        }


        binding.llMon.setOnClickListener {
            currentDate =  days2[0].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv1, listOf(binding.tv2, binding.tv3, binding.tv4, binding.tv5, binding.tv6, binding.tv7))
        }

        binding.llTue.setOnClickListener {
            currentDate =  days2[1].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv2, listOf(binding.tv1, binding.tv3, binding.tv4, binding.tv5, binding.tv6, binding.tv7))
        }

        binding.llWed.setOnClickListener {
            currentDate =  days2[2].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv3, listOf(binding.tv1, binding.tv2, binding.tv4, binding.tv5, binding.tv6, binding.tv7))
        }

        binding.llThu.setOnClickListener {
            currentDate =  days2[3].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv4, listOf(binding.tv1, binding.tv2, binding.tv3, binding.tv5, binding.tv6, binding.tv7))
        }

        binding.llFri.setOnClickListener {
            currentDate =  days2[4].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv5, listOf(binding.tv1, binding.tv2, binding.tv3, binding.tv4, binding.tv6, binding.tv7))
        }

        binding.llSat.setOnClickListener {
            currentDate =  days2[5].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv6, listOf(binding.tv1, binding.tv2, binding.tv3, binding.tv4, binding.tv5, binding.tv7))
        }

        binding.llSun.setOnClickListener {
            currentDate =  days2[6].toString()
            apiTeacherTimeTableList()
            setTextViewColors(binding.tv7, listOf(binding.tv1, binding.tv2, binding.tv3, binding.tv4, binding.tv5, binding.tv6))
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentSelectedDate() {
        currentDate = LocalDate.now().toString()
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        currentDate!!.format(formatter1)

    }

    private fun setTextViewColors(selectedView: TextView, unselectedViews: List<TextView>) {
        val selectedColor = ContextCompat.getColor(applicationContext, R.color.white)
        val unselectedColor = ContextCompat.getColor(applicationContext, R.color.grey_light2)
        val selectedBackground = ContextCompat.getColorStateList(applicationContext, R.color.primary_color)
        val unselectedBackground = ContextCompat.getColorStateList(applicationContext, R.color.white)

        selectedView.setTextColor(selectedColor)
        selectedView.backgroundTintList = selectedBackground

        for (view in unselectedViews) {
            view.setTextColor(unselectedColor)
            view.backgroundTintList = unselectedBackground
        }
    }

    private fun unselectAllTextViews(textViews: List<TextView>) {
        val unselectedColor = ContextCompat.getColor(applicationContext, R.color.grey_light2)
        val unselectedBackground = ContextCompat.getColorStateList(applicationContext, R.color.white)

        for (view in textViews) {
            view.setTextColor(unselectedColor)
            view.backgroundTintList = unselectedBackground
        }
    }

    private fun setupAdapter() {

        mTimeTableAdapter = TimeTableAdapter(mTimeTableModel, applicationContext, this, IsStudentLogin)
        binding.rvTimeTable.adapter = mTimeTableAdapter
        mTimeTableAdapter!!.notifyDataSetChanged()


    }



    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(com.softwill.alpha.R.string.title_timetable)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)
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

    private fun setSpinnerMonth() {
        val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.monthType,
            R.layout.simple_spinner_item
        )
        monthSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthSpinnerAdapter
        binding.spinnerMonth.setSelection(currentMonth - 1)
        binding.spinnerMonth.isEnabled = false  // Disable the spinner
        binding.spinnerMonth.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                /* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                currentMonth = pos


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun detailsCallback(position: Int) {
        TODO("Not yet implemented")
    }

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className: String) {
        mClassId = classId
        mSelectedClassPos = position
        setSubjectAdapter(mSelectedClassPos, subjectId)
        apiTeacherTimeTableList()
    }


    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TimeTableActivity).myApi.api_ClassSubjects()

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
                            mSelectedClassPos = 0
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@TimeTableActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setSubjectAdapter(position: Int, subjectId: Int) {
        mSubjectId = subjectId
        mLectureSubjectAdapter = LectureSubjectAdapterNew(
            this@TimeTableActivity, mLectureClassModel[position].class_subjects, this@TimeTableActivity
        )
        rvLectureSubject?.adapter = mLectureSubjectAdapter
        mLectureSubjectAdapter?.notifyDataSetChanged()
    }

    override fun lectureSubjectClickCallback(subjectId: Int, position: Int) {
        mSubjectId = subjectId
    }

    private fun showCheckboxDropdownDialog(anchorView: View, editText: EditText) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.checkbox_dropdown_layout, null)
        val checkBoxSession1 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession1)
        val checkBoxSession2 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession2)
        val checkBoxSession3 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession3)
        val checkBoxSession4 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession4)
        val checkBoxSession5 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession5)
        val checkBoxSession6 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession6)
        val checkBoxSession7 = dialogView.findViewById<CheckBox>(R.id.checkBoxSession7)
        // Find other checkboxes and set their states as needed

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Done") { _, _ ->
                if (checkBoxSession1.isChecked || checkBoxSession2.isChecked || checkBoxSession3.isChecked ||
                    checkBoxSession4.isChecked || checkBoxSession5.isChecked || checkBoxSession6.isChecked || checkBoxSession7.isChecked
                ) {
                    val selectedOptions = mutableListOf<String>()
                    if (checkBoxSession1.isChecked) {
                        selectedOptions.add("Mon")
                    }
                    if (checkBoxSession2.isChecked) {
                        selectedOptions.add("Tue")
                    }
                    if (checkBoxSession3.isChecked) {
                        selectedOptions.add("Wed")
                    }
                    if (checkBoxSession4.isChecked) {
                        selectedOptions.add("Thu")
                    }
                    if (checkBoxSession5.isChecked) {
                        selectedOptions.add("Fri")
                    }
                    if (checkBoxSession6.isChecked) {
                        selectedOptions.add("Sat")
                    }
                    if (checkBoxSession7.isChecked) {
                        selectedOptions.add("Sun")
                    }
                    val selectedOptionsText = selectedOptions.joinToString(", ")
                    editText.setText(selectedOptionsText)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        val marginDp = 2
        val marginPx = convertDpToPx(marginDp)

        val xOffset = -convertDpToPx(18)
        val yOffset = -convertDpToPx(40)
        val dialogWidth = convertDpToPx(180)
        val dialogHeight = convertDpToPx(280)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        //layoutParams.height = dialogHeight

        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.x = location[0] + xOffset
        layoutParams.y = location[1] + anchorView.height + marginPx + yOffset

        dialog.show()
        dialog.window?.attributes = layoutParams
    }


    private fun convertDpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    private fun apiTeacherTimetableCreate(startTime: String, endTime: String, desc: String, daysDate: String) {

        progressDialog = UtilsFunctions().showCustomProgressDialog(this@TimeTableActivity)

        val timeTableJson = JsonObject().apply {
            addProperty("classId", mClassId)
            addProperty("subjectId", mSubjectId)
            addProperty("startTime", startTime)
            addProperty("endTime", endTime)
            if (mTimeTimeMode == 2) {
                addProperty("scheduleDays", daysDate)
            } else {
                addProperty("scheduleDate", daysDate)
            }
            addProperty("desc", desc)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this).myApi.api_TeacherTimetableCreate(timeTableJson)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Added successfully") {

                            UtilsFunctions().showToast(this@TimeTableActivity, message)
                            apiTeacherTimeTableList()
                        }

                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@TimeTableActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@TimeTableActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }

    private fun apiTeacherTimeTableList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TimeTableActivity).myApi.api_TeacherTimeTableList(currentDate, mClassId);

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val listType = object : TypeToken<List<TimeTableModel>>() {}.type
                        val mList: List<TimeTableModel> = Gson().fromJson(responseBody, listType)

                        mTimeTableModel.clear()
                        mTimeTableModel.addAll(mList)


                        if (!mTimeTableModel.isNullOrEmpty()) {
                            binding.rvTimeTable.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mTimeTableAdapter?.notifyDataSetChanged()
                        } else {
                            binding.rvTimeTable.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.rvTimeTable.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                } else {
                    binding.rvTimeTable.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TimeTableActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}