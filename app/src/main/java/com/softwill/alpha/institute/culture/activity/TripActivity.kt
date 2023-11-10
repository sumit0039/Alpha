package com.softwill.alpha.institute.culture.activity

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
import com.softwill.alpha.institute.culture.adapter.TripsAdapter
import com.softwill.alpha.institute.culture.model.CultureTripModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripActivity : AppCompatActivity() {


    private lateinit var binding: com.softwill.alpha.databinding.ActivityTripBinding
    var mTripsAdapter: TripsAdapter? = null
    private var mDelayHandler: Handler? = null


    val mCultureTripModel = java.util.ArrayList<CultureTripModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_trip
        )



        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiInstituteTrips()
    }

    private fun setupAdapter() {


        mTripsAdapter = TripsAdapter(this@TripActivity, mCultureTripModel)
        binding.rvTrips.adapter = mTripsAdapter
        mTripsAdapter!!.notifyDataSetChanged()


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
        apiInstituteTrips()

    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_trips)
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

    private fun apiInstituteTrips() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TripActivity).myApi.api_InstituteTrips()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CultureTripModel>>() {}.type
                        val mList: List<CultureTripModel> = Gson().fromJson(responseBody, listType)


                        // Update the mTransportTeamMember list with the new data
                        mCultureTripModel.clear()
                        mCultureTripModel.addAll(mList)


                        if (mCultureTripModel.isNotEmpty()) {
                            mTripsAdapter?.notifyDataSetChanged()
                            binding.rvTrips.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvTrips.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTrips.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TripActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}