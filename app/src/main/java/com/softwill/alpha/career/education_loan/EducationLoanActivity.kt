package com.softwill.alpha.career.education_loan

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityEducationLoanBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EducationLoanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEducationLoanBinding
    var mLoanLinkAdapter: LoanLinksAdapter? = null
    var mLoanLinks: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_education_loan)
        setupBack()
        setupAdapter()
        apiEducationLoanDetails()

    }

    private fun setupAdapter() {


        mLoanLinkAdapter = LoanLinksAdapter(applicationContext, mLoanLinks)
        binding.rvLoanLink.adapter = mLoanLinkAdapter
        mLoanLinkAdapter!!.notifyDataSetChanged()


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_education_loan)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


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

    private fun apiEducationLoanDetails() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EducationLoanActivity).myApi.api_EducationLoanDetails()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    if (responseJson != null) {
                        try {
                            val jsonArray = JSONArray(responseJson)

                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)

                                mLoanLinks.clear()
                                val loanLinksArray = jsonObject.getJSONArray("loanLinks")
                                for (i in 0 until loanLinksArray.length()) {
                                    val loanLink = loanLinksArray.getString(i)
                                    mLoanLinks.add(loanLink)
                                }

                                val desc = jsonObject.getString("desc")
                                binding.tvDescription.text = desc

                                mLoanLinkAdapter =
                                    LoanLinksAdapter(this@EducationLoanActivity, mLoanLinks)
                                binding.rvLoanLink.adapter = mLoanLinkAdapter
                                mLoanLinkAdapter!!.notifyDataSetChanged()

                            } else {

                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@EducationLoanActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}