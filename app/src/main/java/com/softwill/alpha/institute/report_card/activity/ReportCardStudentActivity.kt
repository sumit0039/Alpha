package com.softwill.alpha.institute.report_card.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityReportCardStudentBinding
import com.softwill.alpha.institute.report_card.adapter.ReportCardStudentAdapter
import com.softwill.alpha.institute.report_card.model.ReportCard
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportCardStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportCardStudentBinding
    private var mReportCardStudentAdapter: ReportCardStudentAdapter? = null

    private val mReportCard = ArrayList<ReportCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_report_card_student
        )


        setupBack()

        setupReportCardStudentAdapter()


        apiReportCardList()
    }

    private fun setupReportCardStudentAdapter() {
        mReportCardStudentAdapter = ReportCardStudentAdapter(this@ReportCardStudentActivity, mReportCard)
        binding.rvReportCardStudent.adapter = mReportCardStudentAdapter
        mReportCardStudentAdapter?.notifyDataSetChanged()

    }


    private fun apiReportCardList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardStudentActivity).myApi.api_ReportCardList(163);

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val listType = object : TypeToken<List<ReportCard>>() {}.type
                        val mList: List<ReportCard> = Gson().fromJson(responseBody, listType)

                        mReportCard.clear()
                        mReportCard.addAll(mList)


                        if (!mReportCard.isNullOrEmpty()) {
                            binding.rvReportCardStudent.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mReportCardStudentAdapter?.notifyDataSetChanged()
                        }else{
                            binding.rvReportCardStudent.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }else{
                        binding.rvReportCardStudent.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                } else {
                    binding.rvReportCardStudent.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardStudentActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_report_card)
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
}