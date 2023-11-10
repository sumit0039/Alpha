package com.softwill.alpha.profile.privacy.manageNotifications

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityManageNotificationsBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageNotificationsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityManageNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_notifications)


        setupBack()
        onClickListener()
        apiCurrentUserNotificationSettings()


    }

    private fun onClickListener() {

        binding.switchPersonalMessage.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("personalMessage", isChecked)
            }
        }

        binding.switchPost.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("post", isChecked)
            }
        }

        binding.switchAssignment.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("assignment", isChecked)
            }
        }

        binding.switchTeachingPlan.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("teachingPlan", isChecked)
            }
        }

        binding.switchComplaintBox.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("complaintBox", isChecked)
            }
        }

        binding.switchSystemNotifications.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("systemNotification", isChecked)
            }
        }

        binding.switchExam.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed){
                apiUpdateNotificationSettings("exam", isChecked)
            }
        }
    }

    private fun apiUpdateNotificationSettings(type : String , value : Boolean) {
        val jsonObject = JsonObject().apply {
            when (type) {
                "personalMessage" -> addProperty("personalMessage", value)
                "post" -> addProperty("post", value)
                "assignment" -> addProperty("assignment", value)
                "teachingPlan" -> addProperty("teachingPlan", value)
                "complaintBox" -> addProperty("complaintBox", value)
                "systemNotification" -> addProperty("systemNotification", value)
                "exam" -> addProperty("exam", value)
            }
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ManageNotificationsActivity).myApi.api_UpdateNotificationSettings(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Updated successfully") { }

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiCurrentUserNotificationSettings() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ManageNotificationsActivity).myApi.api_CurrentUserNotificationSettings()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)


                    binding.switchPersonalMessage.isChecked =
                        responseObject.getBoolean("personalMessage")
                    binding.switchPost.isChecked = responseObject.getBoolean("post")
                    binding.switchAssignment.isChecked = responseObject.getBoolean("assignment")
                    binding.switchTeachingPlan.isChecked = responseObject.getBoolean("teachingPlan")
                    binding.switchComplaintBox.isChecked = responseObject.getBoolean("complaintBox")
                    binding.switchSystemNotifications.isChecked =
                        responseObject.getBoolean("systemNotification")
                    binding.switchExam.isChecked = responseObject.getBoolean("exam")

                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_manage_notifications)
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

    private fun handleErrorResponse(response: Response<ResponseBody>) {

        val errorResponseJson = response.errorBody()?.string()
        val errorResponseObj = JSONObject(errorResponseJson)
        val errorsArray = errorResponseObj.getJSONArray("errors")
        val errorObj = errorsArray.getJSONObject(0)
        val errorMessage = errorObj.getString("message")

        UtilsFunctions().showToast(this@ManageNotificationsActivity, errorMessage)
    }

}