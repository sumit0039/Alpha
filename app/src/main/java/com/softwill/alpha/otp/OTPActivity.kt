package com.softwill.alpha.otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goodiebag.pinview.Pinview
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.dashboard.DashboardActivity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.signUp.SignUpActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OTPActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityOtpBinding
    var yourPreference: YourPreference? = null
    private var mChangeMobile: Boolean? = true

    // private var mIsStudentLogin: Boolean = false
    lateinit var timer: CountDownTimer
    private var mNumber: String? = null
    private var mOtp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        yourPreference = YourPreference(this)

        setupBack()
        setonClickListener()
        setTimer()

        val bundle: Bundle? = intent.extras
        mNumber = bundle?.getString("mNumber")
        mChangeMobile = bundle?.getBoolean("mChangeMobile")


        binding.tvNumber.text = "+91 $mNumber"


    }

    private fun setTimer() {
         timer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                binding.tvResend.visibility = View.GONE
                binding.tvCounter.visibility = View.VISIBLE
                binding.textView6.text = "Resend code in"
                binding.tvCounter.text = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)
            }

            override fun onFinish() {
                cancelTimer()
            }
        }
        timer.start()
    }

    fun cancelTimer() {
        binding.textView6.text = "I didn’t received a code !"
        binding.tvResend.visibility = View.VISIBLE
        binding.tvCounter.visibility = View.GONE
        timer.cancel()
    }

    private fun setonClickListener() {

        binding.tvResend.setOnClickListener {
            apiResendOtp(mChangeMobile)

        }

        binding.pinview.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                mOtp = pinview!!.value
            }
        })


        binding.btnSubmit.setOnClickListener {

            if (mOtp.toString().trim().isEmpty()) {
                binding.tvError.text = "Enter otp"
            } else if (mOtp.toString().trim().length < 4) {
                binding.tvError.text = "OTP must be 4 digits"
            } else {

                if (mChangeMobile == false) {
                    api_verifyOTP()
                } else {
                    apiChangeMobileOTPVerification()
                }
            }


            /*if (mIsStudentLogin && !mChangeMobile) {
                YourPreference.saveData(Constant.IsStudentLogin, true)
                val intent = Intent(applicationContext, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                YourPreference.saveData(Constant.IsStudentLogin, false)
                val intent = Intent(applicationContext, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }*/




        }
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_otp)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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


    private fun api_verifyOTP() {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", mNumber)
            addProperty("otp", mOtp)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@OTPActivity).myApi.api_VerifyOTP(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")
                    val registration = responseObject.getString("registration")

                    if (message == "Otp verified successfully" && registration == "pending") {
                        val intent = Intent(this@OTPActivity, SignUpActivity::class.java)
                        intent.putExtra("mNumber", mNumber)
                        startActivity(intent)
                        finish()
                    } else if (message == "Otp verified successfully" && registration == "completed") {
                        val userType = responseObject.getString("userType")
                        val token = responseObject.getString("token")
                        val approvedByInstite = responseObject.getString("approvedByInstite")


                        YourPreference.saveData(Constant.AuthToken, token)

                        YourPreference.saveData(Constant.approvedByInstite, approvedByInstite)

                        if (userType == "student") {
                            YourPreference.saveData(Constant.IsStudentLogin, true)
                        } else {
                            YourPreference.saveData(Constant.IsStudentLogin, false)
                        }

                        RetrofitClient.setInstance(this@OTPActivity)
                        YourPreference.saveData(Constant.IsLogin, true)
                        val intent = Intent(this@OTPActivity, DashboardActivity::class.java)
                        intent.putExtra("mNumber", mNumber)
                        startActivity(intent)
                        finish()
                    }

                    //UtilsFunctions().showToast(this@OTPActivity, message)

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@OTPActivity)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiResendOtp(mChangeMobile: Boolean?) {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", mNumber)
        }

        val call: Call<ResponseBody> = if (mChangeMobile == false) {
            RetrofitClient.getInstance(this@OTPActivity).myApi.api_sendOTP(jsonObject)
        } else {
            RetrofitClient.getInstance(this@OTPActivity).myApi.api_ChangeMobileNumber(jsonObject)
        }


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Otp sent successfully") {
                        setTimer()
                        UtilsFunctions().showToast(this@OTPActivity, message)
                    }

                    UtilsFunctions().showToast(this@OTPActivity, message)
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@OTPActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiChangeMobileOTPVerification() {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", mNumber)
            addProperty("otp", mOtp)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@OTPActivity).myApi.api_ChangeMobileOTPVerification(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")


                    if (message == "Updated successfully") {
                        RetrofitClient.setInstance(this@OTPActivity)
                        val intent = Intent(this@OTPActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        UtilsFunctions().showToast(this@OTPActivity, message)
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@OTPActivity)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}