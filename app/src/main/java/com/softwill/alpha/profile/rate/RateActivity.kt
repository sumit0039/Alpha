package com.softwill.alpha.profile.rate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityRateBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RateActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRateBinding
    var ratePlacement: Float = 0f
    var rateStaff: Float = 0f
    var rateTeaching: Float = 0f
    var rateEnvironment: Float = 0f
    var totalRating: Float = 0f
    var yourPreference: YourPreference? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rate)
        yourPreference = YourPreference(this)


        binding.tvTitle.text =
            "Rate your Institute \"${yourPreference?.getData(Constant.instituteName)}\""


        setupBack()

        binding.ratingPlacement.setOnRatingBarChangeListener { _, rating, _ ->
            ratePlacement = rating
            totalRating = (ratePlacement + rateStaff + rateTeaching + rateEnvironment) / 5
            binding.tvPlacement.text = "$rating/5"
            binding.tvTotalRating.text = "$totalRating/5"

        }

        binding.ratingStaff.setOnRatingBarChangeListener { _, rating, _ ->
            rateStaff = rating
            totalRating = (ratePlacement + rateStaff + rateTeaching + rateEnvironment) / 5
            binding.tvStaff.text = "$rating/5"
            binding.tvTotalRating.text = "$totalRating/5"
        }

        binding.ratingTeaching.setOnRatingBarChangeListener { _, rating, _ ->
            rateTeaching = rating
            totalRating = (ratePlacement + rateStaff + rateTeaching + rateEnvironment) / 5
            binding.tvTeaching.text = "$rating/5"
            binding.tvTotalRating.text = "$totalRating/5"
        }

        binding.ratingEnvironment.setOnRatingBarChangeListener { _, rating, _ ->
            rateEnvironment = rating
            totalRating = (ratePlacement + rateStaff + rateTeaching + rateEnvironment) / 4
            binding.tvEnvironment.text = "$rating/5"
            binding.tvTotalRating.text = "$totalRating/5"
        }

        binding.btnSubmit.setOnClickListener(this)

        apiInstituteRating()
    }

    private fun apiSubmitRating() {
        val jsonObject = JsonObject().apply {
            addProperty("placement", ratePlacement)
            addProperty("staff", rateStaff)
            addProperty("teaching", rateTeaching)
            addProperty("environment", rateEnvironment.toString())
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@RateActivity).myApi.api_SubmitRating(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Submitted successfully") {
                        finish()
                    }

                    UtilsFunctions().showToast(this@RateActivity, message)

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@RateActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiInstituteRating() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@RateActivity).myApi.api_InstituteRating()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val ratingResponse = Gson().fromJson(responseJson, RatingResponse::class.java)

                    if (!responseJson.isNullOrEmpty()) {
                        binding.ratingPlacement.rating = ratingResponse.placement.toFloat()
                        binding.ratingStaff.rating = ratingResponse.staff.toFloat()
                        binding.ratingTeaching.rating = ratingResponse.teaching.toFloat()
                        binding.ratingEnvironment.rating = ratingResponse.environment.toFloat()
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@RateActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_rate)
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

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSubmit -> {
                if (ratePlacement <= 0){
                    UtilsFunctions().showToast(this@RateActivity, "Please rate placement")
                }else if (rateStaff <= 0){
                    UtilsFunctions().showToast(this@RateActivity, "Please rate staff")
                }else if (rateTeaching <= 0){
                    UtilsFunctions().showToast(this@RateActivity, "Please rate teaching")
                }else if (rateEnvironment <= 0){
                    UtilsFunctions().showToast(this@RateActivity, "Please rate environment")
                }else{
                    apiSubmitRating()
                }
            }

        }
    }
}