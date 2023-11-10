package com.softwill.alpha.profile.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySettingBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.otp.OTPActivity
import com.softwill.alpha.signIn.SignInActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingBinding
    var yourPreference: YourPreference? = null
    val mFacultiesModel = ArrayList<FacultiesListModel>()
    val mStreamsListModel = ArrayList<StreamsListModel>()
    val mClassesListModel = ArrayList<ClassesListModel>()

    private var mSelectedId: Int = -1
    var mSpinnerList: ArrayList<String> = ArrayList()
    lateinit var spinner: Spinner
    lateinit var etSelectedValue: EditText
    var IsStudentLogin: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        yourPreference = YourPreference(this@SettingActivity)
        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()

        setupBack()

        if (!IsStudentLogin) {
            binding.rlClass.visibility = View.GONE
            binding.rlStream.visibility = View.GONE
            binding.viewClass.visibility = View.GONE
            binding.viewStream.visibility = View.GONE
        }


        binding.tvChangeRole.setOnClickListener(this)
        binding.tvChangeUsername.setOnClickListener(this)
        binding.tvChangeInstitute.setOnClickListener(this)
        binding.tvChangeFaculty.setOnClickListener(this)
        binding.tvChangeStream.setOnClickListener(this)
        binding.tvChangeClass.setOnClickListener(this)
        binding.tvChangeMobile.setOnClickListener(this)
        binding.rlExit.setOnClickListener(this)
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_settings)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvChangeRole -> {
                ChangeRoleBottomSheet()
            }
            R.id.tvChangeUsername -> {
                changeUsernameBottomSheet()
            }
            R.id.tvChangeInstitute -> {
                changeInstituteBottomSheet()
            }
            R.id.tvChangeFaculty -> {
                changeFacultyBottomSheet("facultyId")
            }
            R.id.tvChangeStream -> {
                changeFacultyBottomSheet("streamId")
            }
            R.id.tvChangeClass -> {
                changeFacultyBottomSheet("classId")
            }
            R.id.tvChangeMobile -> {
                changeMobileBottomSheet()
            }
            R.id.rlExit -> {
                LogoutBottomSheet()
            }

        }
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

    @SuppressLint("SetTextI18n")
    private fun changeUsernameBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_username, null)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val tvIsNotAvailable = view.findViewById<TextView>(R.id.tvIsNotAvailable)

        if (yourPreference?.getData(Constant.userName) != null && yourPreference?.getData(Constant.userName) != "null") {
            etUsername.setText("${yourPreference?.getData(Constant.userName)}")
        }

        etUsername.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tvIsNotAvailable.visibility = View.INVISIBLE
            }
        })


        btnChange.setOnClickListener {

            if (etUsername.text.toString().trim().isEmpty()) {
                tvIsNotAvailable.text = "Username can't be empty"
                tvIsNotAvailable.visibility = View.VISIBLE
            } else if (etUsername.text.toString().trim().length < 3) {
                tvIsNotAvailable.text = "Username must contains 3 letters"
                tvIsNotAvailable.visibility = View.VISIBLE
            } else {
                apiUpdateProfileSettings("userName", etUsername.text.toString().trim(), dialog)
            }

        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }

    private fun changeInstituteBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_institute, null)


        val btnChange = view.findViewById<Button>(R.id.btnChange)

        btnChange.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }

    private fun changeFacultyBottomSheet(type: String) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_faculty_stream_class, null)


        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubTitle = view.findViewById<TextView>(R.id.tvSubTitle)
        val btnChange = view.findViewById<Button>(R.id.btnChange)
        spinner = view.findViewById(R.id.spinner)
        etSelectedValue = view.findViewById(R.id.etSelectedValue)


        if (type == "facultyId") {
            tvTitle.text = getString(R.string.change_faculty)
            tvSubTitle.text = getString(R.string.select_faculty)
        } else if (type == "streamId") {
            tvTitle.text = getString(R.string.change_stream)
            tvSubTitle.text = getString(R.string.select_stream)
        } else if (type == "classId") {
            tvTitle.text = getString(R.string.change_class)
            tvSubTitle.text = getString(R.string.select_class)
        }


        apiFacultyStreamClassList(type)

        btnChange.setOnClickListener {
            apiUpdateProfileSettings(type, mSelectedId.toString(), dialog)
        }



        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }


    private fun changeMobileBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_mobile, null)


        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val etNumber = view.findViewById<EditText>(R.id.etNumber)
        val tvError = view.findViewById<TextView>(R.id.tvError)


        btnChange.setOnClickListener {

            if (etNumber.text.toString().trim().isEmpty()) {
                tvError.setText("Enter mobile number")
            } else if (etNumber.text.toString().trim().length < 10) {
                tvError.setText("Enter valid mobile number")
            } else {
                apiChangeMobileNumber(etNumber.text.toString().trim(), dialog);

            }


        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }


    private fun LogoutBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_logout, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog.dismiss()
            apiLogout()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun ChangeRoleBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_role, null)


        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val tvChangeString = view.findViewById<TextView>(R.id.tvChangeString)


        val wordtoSpan: Spannable =
            SpannableString("Enter “Student” in the box")

        wordtoSpan.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_color)), 6, 14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvChangeString.setText(wordtoSpan)



        btnChange.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun apiChangeMobileNumber(value: String, dialog: BottomSheetDialog) {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SettingActivity).myApi.api_ChangeMobileNumber(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        // Updated successfully
                        var message = responseObject.getString("message")
                        if (message == "Otp sent successfully") {

                            val intent = Intent(this@SettingActivity, OTPActivity::class.java)
                            intent.putExtra("mNumber", value)
                            intent.putExtra("mChangeMobile", true)
                            startActivity(intent)

                        } else {
                            UtilsFunctions().showToast(this@SettingActivity, message)
                        }

                        dialog.dismiss()
                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiUpdateProfileSettings(type: String, value: String, dialog: BottomSheetDialog) {
        val jsonObject = JsonObject().apply {
            when (type) {
                "userName" -> addProperty("userName", value)
                "instituteId" -> addProperty("instituteId", value)
                "facultyId" -> addProperty("facultyId", value)
                "streamId" -> addProperty("streamId", value)
                "classId" -> addProperty("classId", value)
            }
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SettingActivity).myApi.api_UpdateProfileSettings(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    dialog.dismiss()

                    if (responseObject.has("message")) {

                        if (type == "userName") {
                            YourPreference.saveData(Constant.userName, value)
                        } else if (type == "facultyId") {
                            YourPreference.saveData(Constant.instituteFacultyId, value)
                            YourPreference.saveData(
                                Constant.facultyName,
                                etSelectedValue.text.toString().trim()
                            )
                        } else if (type == "streamId") {
                            YourPreference.saveData(Constant.instituteStreamId, value)
                            YourPreference.saveData(
                                Constant.streamName,
                                etSelectedValue.text.toString().trim()
                            )
                        } else if (type == "classId") {
                            YourPreference.saveData(Constant.instituteStreamClassId, value)
                            YourPreference.saveData(
                                Constant.className,
                                etSelectedValue.text.toString().trim()
                            )
                        }

                        UtilsFunctions().showToast(
                            this@SettingActivity,
                            responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiLogout() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SettingActivity).myApi.api_logout()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        // Updated successfully

                        YourPreference.clearPreference(this@SettingActivity)
                        YourPreference.saveData(Constant.AuthToken, "")
                        YourPreference.saveData(Constant.IsLogin, false)



                        UtilsFunctions().showToast(
                            this@SettingActivity,
                            responseObject.getString("message")
                        )

                        val intent = Intent(applicationContext, SignInActivity::class.java)
                        startActivity(intent)
                        finishAffinity()


                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiFacultyStreamClassList(type: String) {

        val retrofit = RetrofitClient.getInstance(this@SettingActivity).myApi
        val call: Call<ResponseBody> = when (type) {
            "facultyId" -> retrofit.api_FacultiesList(
                Integer.parseInt(
                    yourPreference?.getData(
                        Constant.instituteId
                    ).toString()
                )
            )
            "streamId" -> retrofit.api_StreamsList(
                Integer.parseInt(
                    yourPreference?.getData(Constant.instituteFacultyId).toString()
                )
            )
            "classId" -> retrofit.api_ClassesList(
                Integer.parseInt(
                    yourPreference?.getData(Constant.instituteStreamId).toString()
                )
            )
            else -> throw IllegalArgumentException("Invalid type: $type")
        }



        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        mFacultiesModel.clear()
                        mStreamsListModel.clear()
                        mSpinnerList.clear()


                        try {
                            val jsonArray = JSONArray(responseJson)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                var id: Int
                                var name: String

                                when (type) {
                                    "facultyId" -> {
                                        id = jsonObject.getInt("facultyId")
                                        name = jsonObject.getString("name")
                                        mFacultiesModel.add(FacultiesListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                    "streamId" -> {
                                        id = jsonObject.getInt("streamId")
                                        name = jsonObject.getString("streamName")
                                        mStreamsListModel.add(StreamsListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                    "classId" -> {
                                        id = jsonObject.getInt("classId")
                                        name = jsonObject.getString("className")
                                        mClassesListModel.add(ClassesListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                }

                            }

                            if (mSpinnerList.isNotEmpty()) {
                                setSpinner(type)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    } else {
                        mFacultiesModel.clear()
                        mStreamsListModel.clear()
                        mClassesListModel.clear()
                        mSelectedId = -1;
                        mSpinnerList.clear()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setSpinner(type: String) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mSpinnerList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spinner.adapter = adapter
        if (type == "facultyId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.facultyName)))
        } else if (type == "streamId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.streamName)))
        } else if (type == "classId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.className)))
        }
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                val value = adapterView.getItemAtPosition(pos).toString()
                etSelectedValue.setText(spinner.selectedItem.toString())
                if (type == "facultyId") {
                    mSelectedId = mFacultiesModel[pos].facultyId
                } else if (type == "streamId") {
                    mSelectedId = mStreamsListModel[pos].streamId
                } else if (type == "classId") {
                    mSelectedId = mClassesListModel[pos].classId
                }

                System.out.println("$type : $mSelectedId")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

}