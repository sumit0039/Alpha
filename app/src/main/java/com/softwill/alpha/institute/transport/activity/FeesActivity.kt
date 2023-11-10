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
import com.softwill.alpha.institute.transport.adapter.FeesAdapter
import com.softwill.alpha.institute.transport.model.TransportFees
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeesActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityFeesBinding
    private var mDelayHandler: Handler? = null
    var mFeesAdapter: FeesAdapter? = null

    val mTransportFees = java.util.ArrayList<TransportFees>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_fees)


        setupBack()
        setupSwipeListener()
        setupAdapter()

    }

    private fun setupAdapter() {

        mFeesAdapter = FeesAdapter(this@FeesActivity, mTransportFees)
        binding.rvFees.adapter = mFeesAdapter
        mFeesAdapter!!.notifyDataSetChanged()
        apiTransportFees()

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
        apiTransportFees()
    }

    private fun apiTransportFees() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@FeesActivity).myApi.api_TransportFees()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<TransportFees>>() {}.type
                        val mList  : List<TransportFees> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mTransportFees.clear()
                        mTransportFees.addAll(mList)


                        if (mTransportFees.isNotEmpty()){
                            mFeesAdapter?.notifyDataSetChanged()
                            binding.rvFees.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        }else{
                            binding.rvFees.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvFees.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@FeesActivity)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_fees)
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