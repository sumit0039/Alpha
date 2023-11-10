package com.softwill.alpha.profile.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.ImagePicker.Companion.getError
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.editProfile.adapter.AchievementsAdapter
import com.softwill.alpha.profile.editProfile.adapter.HobbiesAdapter
import com.softwill.alpha.profile.editProfile.adapter.SkillsAdapter
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity(), AchievementsAdapter.AchievementsCallbackInterface, SkillsAdapter.SkillsCallbackInterface , HobbiesAdapter.HobbiesCallbackInterface{

    private lateinit var binding: com.softwill.alpha.databinding.ActivityEditProfileBinding
    var yourPreference: YourPreference? = null

    var mHobbiesAdapter: HobbiesAdapter? = null
    private val mHobbiesList: MutableList<String> = mutableListOf()

    var mSkillsAdapter: SkillsAdapter? = null
    private val mSkillsList: MutableList<String> = mutableListOf()

    var mAchievementsAdapter: AchievementsAdapter? = null
    private val mAchievementsList: MutableList<String> = mutableListOf()


    var picker: DatePickerDialog? = null

    var imagePath: String? = null
    private var vGender: String = "other"
    private var vBloodGroup: String = "A+"

    private val PERMISSION_CODE = 100
    var launcher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        yourPreference = YourPreference(this)


        setupBack()
        onClickListener()
        setProfile()
        setAdapter()


        launcher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data
                imagePath = uri?.path?.let { File(it).toString() }
                println("IMAGEPATH : $imagePath")
                Glide.with(this).load(imagePath).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                if(!imagePath.isNullOrEmpty()){
                    apiCurrentUserChangeProfilePicture(imagePath)
                }


            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, getError(result.data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {

        mHobbiesAdapter = HobbiesAdapter(mHobbiesList, applicationContext, this)
        binding.rvHobbies.adapter = mHobbiesAdapter
        mHobbiesAdapter?.notifyDataSetChanged()


        mSkillsAdapter = SkillsAdapter(mSkillsList, applicationContext, this)
        binding.rvSkills.adapter = mSkillsAdapter
        mSkillsAdapter?.notifyDataSetChanged()



        mAchievementsAdapter = AchievementsAdapter(mAchievementsList, applicationContext, this)
        binding.rvAchievements.adapter = mAchievementsAdapter
        mAchievementsAdapter?.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClickListener() {
        binding.etDateOfBirth.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            picker = DatePickerDialog(
                this@EditProfileActivity,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                   //binding.etDateOfBirth.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1))
                    binding.etDateOfBirth.setText(String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth))
                },
                year,
                month,
                day
            )

            picker?.datePicker?.maxDate = System.currentTimeMillis() + 1000
            if(binding.etDateOfBirth.text.toString().isNotEmpty()){
                val updateDate: List<String> = binding.etDateOfBirth.text.toString().split("-")
                picker?.updateDate(updateDate[0].toInt(), updateDate[1].toInt() - 1, updateDate[2].toInt())
            }
            picker?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            picker?.show()
        }



        binding.cardInformation.setOnClickListener {
            handleCardClick(binding.cardInformation, binding.cardAboutMe, binding.tvInformation, binding.tvAboutMe, binding.llInformation, binding.llAboutMe)
        }

        binding.cardAboutMe.setOnClickListener {
            handleCardClick(binding.cardAboutMe, binding.cardInformation, binding.tvAboutMe, binding.tvInformation, binding.llAboutMe, binding.llInformation)
        }


        binding.btnMale.setOnClickListener {
            setGenderSelection(binding.btnMale, "male")
        }

        binding.btnFemale.setOnClickListener {
            setGenderSelection(binding.btnFemale, "female")
        }

        binding.btnOther.setOnClickListener {
            setGenderSelection(binding.btnOther, "other")
        }


        binding.ivProfileImage.setOnClickListener {
            askStoragePermission()
        }

        binding.btnSaveInfo.setOnClickListener{

            var firstName = binding.etFirstName.text.toString().trim()
            var lastName = binding.etLastName.text.toString().trim()
            var email = binding.etEmailAddress.text.toString().trim()
            var dateOfBirth = binding.etDateOfBirth.text.toString().trim()

            if (firstName.isEmpty()){
                UtilsFunctions().showToast(this@EditProfileActivity, "First name can't be empty")
            }else if (firstName.length < 3){
                UtilsFunctions().showToast(this@EditProfileActivity, "First name must contains 3 letters")
            }else if (lastName.isEmpty()){
                UtilsFunctions().showToast(this@EditProfileActivity, "Last name can't be empty")
            }else if (lastName.length < 3){
                UtilsFunctions().showToast(this@EditProfileActivity, "Last name must contains 3 letters")
            } else if (email.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Email address can't be empty")
            } else if (!isValidEmail(email)) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Invalid email address")
            } else if (dateOfBirth.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Date of birth can't be empty")
            } else {
                apiCurrentUserUpdateProfile()
            }
        }

        binding.btnSaveBio.setOnClickListener {
            var bio = binding.etBio.text.toString().trim()

            if (bio.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Bio can't be empty")
            } else if (bio.length < 20) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Bio must contains 20 letters")
            } else {
                apiCurrentUserUpdateBio()
            }
        }


        binding.tvAddHobbies.setOnClickListener {
            var value = binding.etHobbies.text.toString().trim()

            if (value.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Hobby can't be empty")
            } else if (value.length < 3){
                UtilsFunctions().showToast(this@EditProfileActivity, "Hobby must contains 3 letters")
            }else {
                mHobbiesList.add(mHobbiesList.size , value)
                mHobbiesAdapter?.notifyDataSetChanged()
                binding.etHobbies.text.clear()

            }
        }

        binding.tvAddSkills.setOnClickListener {
            var value = binding.etSkill.text.toString().trim()

            if (value.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Skill can't be empty")
            } else if (value.length < 3){
                UtilsFunctions().showToast(this@EditProfileActivity, "Skill must contains 3 letters")
            }else {
                mSkillsList.add(mSkillsList.size , value)
                mSkillsAdapter?.notifyDataSetChanged()
                binding.etSkill.text.clear()

            }
        }

        binding.tvAddAchievements.setOnClickListener {
            var value = binding.etAchievement.text.toString().trim()

            if (value.isEmpty()) {
                UtilsFunctions().showToast(this@EditProfileActivity, "Achievement can't be empty")
            } else if (value.length < 3){
                UtilsFunctions().showToast(this@EditProfileActivity, "Achievement must contains 3 letters")
            }else {
                mAchievementsList.add(mAchievementsList.size , value)
                mAchievementsAdapter?.notifyDataSetChanged()
                binding.etAchievement.text.clear()

            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    private fun setGenderSelection(button: TextView, gender: String) {
        setButtonState(button)
        vGender = gender
    }

    private fun setButtonState(selectedButton: TextView) {
        val buttons = listOf(binding.btnMale, binding.btnFemale, binding.btnOther)

        for (button in buttons) {
            val isSelected = button == selectedButton
            button.setBackgroundResource(if (isSelected) R.drawable.bg_button else R.drawable.bg_rounded_1)
            button.setTextColor(resources.getColor(if (isSelected) R.color.white else R.color.black))
        }
    }

    private fun handleCardClick(selectedCard: CardView, deselectedCard: CardView, selectedTextView: TextView, deselectedTextView: TextView, selectedLayout: View, deselectedLayout: View) {
        selectedCard.setCardBackgroundColor(resources.getColor(R.color.primary_color))
        deselectedCard.setCardBackgroundColor(resources.getColor(R.color.button_color))

        selectedTextView.setTextColor(resources.getColor(R.color.white))
        deselectedTextView.setTextColor(resources.getColor(R.color.black))

        selectedLayout.visibility = View.VISIBLE
        deselectedLayout.visibility = View.GONE
    }

    private fun setSpinnerBloodGroup(selectedValue: String?) {
        val bloodGroupArray = resources.getStringArray(R.array.BloodGroup)
        val position = bloodGroupArray.indexOf(selectedValue)

        val monthSpinnerAdapter = ArrayAdapter(
            applicationContext,
            R.layout.simple_spinner_item,
            bloodGroupArray
        )
        monthSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerBloodType.adapter = monthSpinnerAdapter

        if (position != -1) {
            binding.spinnerBloodType.setSelection(position)
        } else {
            // Handle the case where the selected value is not found in the array
            // For example, set a default selection or show an error message
            binding.spinnerBloodType.setSelection(0)
        }

        binding.spinnerBloodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, l: Long) {
                vBloodGroup = adapterView.getItemAtPosition(pos).toString()
                // Handle the selected item change
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle the case where nothing is selected
            }
        }
    }


    private fun askStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@EditProfileActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_CODE
            )
        } else {
            ImagePicker.Companion.with(this@EditProfileActivity)
                .crop()
                .cropOval()

                .maxResultSize(512, 512, true)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog { launcher?.launch(it) }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.Companion.with(this@EditProfileActivity)
                    .crop()
                    .cropOval()
                    .maxResultSize(512, 512, true)
                    .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                    .createIntentFromDialog { launcher?.launch(it) }
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_edit)
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


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setProfile() {
        //Information
        var avtarUrl = yourPreference?.getData(Constant.avtarUrl)
        val gender = yourPreference?.getData(Constant.gender)
        val email = yourPreference?.getData(Constant.email)
        val dob = yourPreference?.getData(Constant.dob)

        binding.etFirstName.setText(yourPreference?.getData(Constant.firstName))
        binding.etLastName.setText(yourPreference?.getData(Constant.lastName))
        binding.etNumber.setText(yourPreference?.getData(Constant.mobile))

        if(!avtarUrl.isNullOrEmpty() && avtarUrl != "null"){
            Glide.with(this@EditProfileActivity).load(avtarUrl)
                .placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)
        }


        if (email != null && email != "null") {
            binding.etEmailAddress.setText(email)
        }
        if (dob != null && dob != "null") {
            binding.etDateOfBirth.setText(dob)
        }

        when (gender) {
            "male" -> {
                setGenderSelection(binding.btnMale, "male")
            }
            "female" -> {
                setGenderSelection(binding.btnFemale, "female")
            }
            else -> {
                setGenderSelection(binding.btnOther, "other")
            }
        }

        setSpinnerBloodGroup(yourPreference?.getData(Constant.bloodGroup))


        //About Me
        val bio = yourPreference?.getData(Constant.bio)


        if (bio != null && bio != "null") {
            binding.etBio.setText(yourPreference?.getData(Constant.bio))
        }


        val achievementsElements = yourPreference?.getData(Constant.achievementsList)
            ?.removeSurrounding("[", "]") // Remove the square brackets
            ?.split(",")

        if (!achievementsElements.isNullOrEmpty() && achievementsElements.any() { it.isNotBlank() }) {
            mAchievementsList.clear()
            mAchievementsList.addAll(achievementsElements.map { it.trim().removeSurrounding("\"") })
            mAchievementsAdapter?.notifyDataSetChanged()
        }



        val skillsElements = yourPreference?.getData(Constant.skillsList)
            ?.removeSurrounding("[", "]") // Remove the square brackets
            ?.split(",")

        if (!skillsElements.isNullOrEmpty() && skillsElements.any() {it.isNotBlank()}) {
            mSkillsList.clear()
            mSkillsList.addAll(skillsElements.map { it.trim().removeSurrounding("\"") })
            mSkillsAdapter?.notifyDataSetChanged()
        }

        val hobbiesElements = yourPreference?.getData(Constant.hobbiesList)
            ?.removeSurrounding("[", "]") // Remove the square brackets
            ?.split(",")

        if (!hobbiesElements.isNullOrEmpty() && hobbiesElements.any { it.isNotBlank() }) {
            mHobbiesList.clear()
            mHobbiesList.addAll(hobbiesElements.map { it.trim().removeSurrounding("\"") })
            mHobbiesAdapter?.notifyDataSetChanged()
        }

    }

    private fun apiCurrentUserUpdateProfile() {
        val jsonObject = JsonObject().apply {
            addProperty("firstName", binding.etFirstName.text.toString().trim())
            addProperty("lastName", binding.etLastName.text.toString().trim())
            addProperty("email", binding.etEmailAddress.text.toString().trim())
            addProperty("dob", binding.etDateOfBirth.text.toString().trim())
            addProperty("gender", vGender)
            addProperty("bloodGroup", vBloodGroup)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EditProfileActivity).myApi.api_CurrentUserUpdateProfile(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Updated successfully") {

                        YourPreference.saveData(Constant.firstName, binding.etFirstName.text.toString().trim())
                        YourPreference.saveData(Constant.lastName,  binding.etLastName.text.toString().trim())
                        YourPreference.saveData(Constant.email, binding.etEmailAddress.text.toString().trim())
                        YourPreference.saveData(Constant.dob, binding.etDateOfBirth.text.toString().trim())
                        YourPreference.saveData(Constant.gender, vGender)
                        YourPreference.saveData(Constant.bloodGroup, vBloodGroup)

                        finish()
                    }

                    UtilsFunctions().showToast(this@EditProfileActivity, message)

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiCurrentUserUpdateBio() {
        val jsonObject = JsonObject().apply {
            addProperty("bio", binding.etBio.text.toString().trim())
            add("hobbies", mHobbiesList.toJsonArray())
            add("skills", mSkillsList.toJsonArray())
            add("achievements", mAchievementsList.toJsonArray())
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EditProfileActivity).myApi.api_CurrentUserUpdateBio(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Updated successfully") {

                        YourPreference.saveData(Constant.bio, binding.etBio.text.toString().trim())
                        YourPreference.saveData(Constant.achievementsList, mAchievementsList.toString())
                        YourPreference.saveData(Constant.skillsList, mSkillsList.toString())
                        YourPreference.saveData(Constant.hobbiesList, mHobbiesList.toString())

                        finish()
                    }

                    UtilsFunctions().showToast(this@EditProfileActivity, message)

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun handleErrorResponse(response: Response<ResponseBody>) {

        val errorResponseJson = response.errorBody()?.string()
        val errorResponseObj = JSONObject(errorResponseJson)
        val errorsArray = errorResponseObj.getJSONArray("errors")
        val errorObj = errorsArray.getJSONObject(0)
        val errorMessage = errorObj.getString("message")

        UtilsFunctions().showToast(this@EditProfileActivity, errorMessage)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteAchievementsCallback(position: Int) {
        mAchievementsList.removeAt(position)
        mAchievementsAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteSkillsCallback(position: Int) {
        mSkillsList.removeAt(position)
        mSkillsAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteHobbiesCallback(position: Int) {
        mHobbiesList.removeAt(position)
        mHobbiesAdapter?.notifyDataSetChanged()
    }

    fun List<String>.toJsonArray(): JsonArray = JsonArray().apply {
        this@toJsonArray.forEach { add(it) }
    }

    private fun apiCurrentUserChangeProfilePicture(imagePath: String?) {


        var file = imagePath?.let { File(it) }

        val fileToUpload = file?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it) }?.let {
            MultipartBody.Part.createFormData(
                "profile",
                file.name,
                it
            )
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EditProfileActivity).myApi.api_CurrentUserChangeProfilePicture(fileToUpload
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Updated successfully") {
                            UtilsFunctions().showToast(this@EditProfileActivity, message)
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@EditProfileActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@EditProfileActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



}