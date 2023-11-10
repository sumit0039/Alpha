package com.softwill.alpha.institute.canteen.activity

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
import com.softwill.alpha.databinding.ActivityFacilitiesBinding
import com.softwill.alpha.institute.canteen.adapter.FacilitiesCanteenAdapter
import com.softwill.alpha.institute.canteen.model.CanteenFacility
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacilitiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacilitiesBinding
    private var mDelayHandler: Handler? = null
    var mFacilitiesCanteenAdapter: FacilitiesCanteenAdapter? = null


    val mCanteenFacility = java.util.ArrayList<CanteenFacility>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_facilities)




        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiCanteenFacilities()

    }

    private fun setupAdapter() {


        mFacilitiesCanteenAdapter = FacilitiesCanteenAdapter(this@FacilitiesActivity, mCanteenFacility)
        binding.rvFacilities.adapter = mFacilitiesCanteenAdapter
        mFacilitiesCanteenAdapter!!.notifyDataSetChanged()


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
        apiCanteenFacilities()
    }


    private fun apiCanteenFacilities() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@FacilitiesActivity).myApi.api_CanteenFacilities()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CanteenFacility>>() {}.type
                        val mList: List<CanteenFacility> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mCanteenFacility.clear()
                        mCanteenFacility.addAll(mList)


                        if (mCanteenFacility.isNotEmpty()) {
                            mFacilitiesCanteenAdapter?.notifyDataSetChanged()
                            binding.rvFacilities.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvFacilities.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvFacilities.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@FacilitiesActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }





    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_facilities)
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