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
import com.softwill.alpha.databinding.ActivityCompetitionsBinding
import com.softwill.alpha.institute.sport.adapter.CompetitionsAdapter
import com.softwill.alpha.institute.sport.model.SportCompetitions
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompetitionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompetitionsBinding
    private var mDelayHandler: Handler? = null
    var mCompetitionsAdapter: CompetitionsAdapter? = null

    val mSportCompetitions = java.util.ArrayList<SportCompetitions>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_competitions)

        setupBack()

        setupSwipeListener()
        setupAdapter()
        apiSportCompetitions()
    }

    private fun setupAdapter() {


        mCompetitionsAdapter = CompetitionsAdapter(this@CompetitionsActivity, mSportCompetitions)
        binding.rvCompetitions.adapter = mCompetitionsAdapter
        mCompetitionsAdapter!!.notifyDataSetChanged()


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
        apiSportCompetitions()
    }


    private fun apiSportCompetitions() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CompetitionsActivity).myApi.api_SportCompetitions()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SportCompetitions>>() {}.type
                        val mList: List<SportCompetitions> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mSportCompetitions.clear()
                        mSportCompetitions.addAll(mList)


                        if (mSportCompetitions.isNotEmpty()) {
                            mCompetitionsAdapter?.notifyDataSetChanged()
                            binding.rvCompetitions.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvCompetitions.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvCompetitions.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@CompetitionsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_compititions)
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