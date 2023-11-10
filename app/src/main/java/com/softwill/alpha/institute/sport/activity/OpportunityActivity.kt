package com.softwill.alpha.institute.sport.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityOpportunityBinding
import com.softwill.alpha.institute.sport.adapter.OpportunityAdapter
import com.softwill.alpha.institute.sport.model.SportOpportunity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpportunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpportunityBinding
    private var mDelayHandler: Handler? = null
    var mOpportunityAdapter: OpportunityAdapter? = null


    val mSportOpportunity = java.util.ArrayList<SportOpportunity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_opportunity)


        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiSportOpportunities()
    }


    private fun setupAdapter() {


        mOpportunityAdapter = OpportunityAdapter(applicationContext, mSportOpportunity)
        binding.rvOpportunity.adapter = mOpportunityAdapter
        mOpportunityAdapter!!.notifyDataSetChanged()


    }

    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiSportOpportunities()
    }


    private fun apiSportOpportunities() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@OpportunityActivity).myApi.api_SportOpportunities()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SportOpportunity>>() {}.type
                        val mList: List<SportOpportunity> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mSportOpportunity.clear()
                        mSportOpportunity.addAll(mList)


                        if (mSportOpportunity.isNotEmpty()) {
                            mOpportunityAdapter?.notifyDataSetChanged()
                            binding.rvOpportunity.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvOpportunity.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvOpportunity.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@OpportunityActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_opportunity)
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