package com.softwill.alpha.signIn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.help.HelpActivity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.otp.OTPActivity
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : AppCompatActivity() {


    private lateinit var binding: com.softwill.alpha.databinding.ActivitySignInBinding
    var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)


        setupBack()
        setupOnClickListener()


    }

    private fun setupOnClickListener() {
        binding.btnSendOTP.setOnClickListener {
            if (binding.etNumber.text.toString().trim().isEmpty()) {
                binding.tvError.setText("Enter mobile number")
            } else if (binding.etNumber.text.toString().trim().length < 10) {
                binding.tvError.setText("Enter valid mobile number")
            } else {
                binding.etNumber.clearFocus()
                apiSenOtp();
            }

        }

        binding.etNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvError.setText("");
            }
        })



        binding.tvHelp.setOnClickListener {
            val intent = Intent(applicationContext, HelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBack() {
        if (supportActionBar != null) {
            supportActionBar?.hide();
        }
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }


    private fun apiSenOtp() {
        val jsonObject = JsonObject().apply {
            addProperty("mobile", binding.etNumber.text.toString().trim())
        }

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SignInActivity).myApi.api_sendOTP(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)


                    if (responseObject.has("message")) {
                        val message = responseObject.getString("message")

                        if (message == "Otp sent successfully") {
                            val intent = Intent(this@SignInActivity, OTPActivity::class.java)
                            intent.putExtra("mNumber", binding.etNumber.text.toString().trim())
                            intent.putExtra("mChangeMobile", false)
                            startActivity(intent)
                            binding.etNumber.text.clear()

                        }


                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SignInActivity, responseObject.getString("error")
                        )
                    }


                } else {


                    UtilsFunctions().handleErrorResponse(response, this@SignInActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}