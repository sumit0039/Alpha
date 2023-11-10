package com.softwill.alpha.profile.privacy

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityPrivacyBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.privacy.blockedPeople.BlockedPeopleActivity
import com.softwill.alpha.profile.privacy.manageNotifications.ManageNotificationsActivity
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy)



        setupBack()
        onClickListener()
        apiCurrentUserPrivacySettings()


    }

    private fun onClickListener() {
        binding.cardManageNotifications.setOnClickListener {
            val intent = Intent(applicationContext, ManageNotificationsActivity::class.java)
            startActivity(intent)
        }


        binding.cardBlockedPeople.setOnClickListener {
            val intent = Intent(applicationContext, BlockedPeopleActivity::class.java)
            startActivity(intent)
        }

        binding.switchProfilePhoto.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiCurrentUserUpdateProfile("profilePicture", isChecked)
            }
        }

        binding.switchMobileNumber.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiCurrentUserUpdateProfile("mobileNumber", isChecked)
            }
        }

        binding.switchEmailAddress.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiCurrentUserUpdateProfile("email", isChecked)
            }
        }

        binding.switchDateOfBirth.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiCurrentUserUpdateProfile("dob", isChecked)
            }
        }

        binding.switchAboutMe.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiCurrentUserUpdateProfile("aboutBio", isChecked)
            }
        }
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_privacy)
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


    private fun apiCurrentUserPrivacySettings() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@PrivacyActivity).myApi.api_CurrentUserPrivacySettings()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)


                    binding.switchProfilePhoto.isChecked = responseObject.getBoolean("profilePicture")
                    binding.switchMobileNumber.isChecked = responseObject.getBoolean("mobileNumber")
                    binding.switchEmailAddress.isChecked = responseObject.getBoolean("email")
                    binding.switchDateOfBirth.isChecked = responseObject.getBoolean("dob")
                    binding.switchAboutMe.isChecked = responseObject.getBoolean("aboutBio")

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiCurrentUserUpdateProfile(type : String , value : Boolean) {
        val jsonObject = JsonObject().apply {
            when (type) {
                "profilePicture" -> addProperty("profilePicture", value)
                "mobileNumber" -> addProperty("mobileNumber", value)
                "email" -> addProperty("email", value)
                "dob" -> addProperty("dob", value)
                "aboutBio" -> addProperty("aboutBio", value)
            }
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PrivacyActivity).myApi.api_UpdatePrivacySettings(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Updated successfully") {

                    }

                   // UtilsFunctions().showToast(this@PrivacyActivity, message)

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

        UtilsFunctions().showToast(this@PrivacyActivity, errorMessage)
    }

}