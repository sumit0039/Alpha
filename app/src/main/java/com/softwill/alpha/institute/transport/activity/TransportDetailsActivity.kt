package com.softwill.alpha.institute.transport.activity

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
import com.softwill.alpha.institute.transport.adapter.TransportDetailsAdapter
import com.softwill.alpha.institute.transport.model.TransportDetails
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransportDetailsActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityTransportDetailsBinding
    private var mDelayHandler: Handler? = null
    var mTransportDetailsAdapter: TransportDetailsAdapter? = null

    val mTransportDetails = java.util.ArrayList<TransportDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_transport_details
        )

        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiTransportDetails()

    }

    private fun setupAdapter() {

        mTransportDetailsAdapter =
            TransportDetailsAdapter(this@TransportDetailsActivity, mTransportDetails)
        binding.rvTransportDetails.adapter = mTransportDetailsAdapter
        mTransportDetailsAdapter!!.notifyDataSetChanged()


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
        apiTransportDetails()
    }

    private fun apiTransportDetails() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TransportDetailsActivity).myApi.api_TransportDetails()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<TransportDetails>>() {}.type
                        val mList: List<TransportDetails> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mTransportDetails.clear()
                        mTransportDetails.addAll(mList)


                        if (mTransportDetails.isNotEmpty()) {
                            mTransportDetailsAdapter?.notifyDataSetChanged()
                            binding.rvTransportDetails.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvTransportDetails.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTransportDetails.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TransportDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_transport_details)
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