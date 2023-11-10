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
import com.softwill.alpha.databinding.ActivityAccessoriesBinding
import com.softwill.alpha.institute.sport.adapter.AccessoriesAdapter
import com.softwill.alpha.institute.sport.model.SportAccessories
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccessoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessoriesBinding
    private var mDelayHandler: Handler? = null
    var mAccessoriesAdapter: AccessoriesAdapter? = null

    val mSportAccessories = java.util.ArrayList<SportAccessories>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accessories)

        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiSportAccessories()

    }

    private fun setupAdapter() {


        mAccessoriesAdapter = AccessoriesAdapter(applicationContext, mSportAccessories)
        binding.rvAccessories.adapter = mAccessoriesAdapter
        mAccessoriesAdapter!!.notifyDataSetChanged()


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
        apiSportAccessories()
    }

    private fun apiSportAccessories() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@AccessoriesActivity).myApi.api_SportAcceseries()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SportAccessories>>() {}.type
                        val mList: List<SportAccessories> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mSportAccessories.clear()
                        mSportAccessories.addAll(mList)


                        if (mSportAccessories.isNotEmpty()) {
                            mAccessoriesAdapter?.notifyDataSetChanged()
                            binding.rvAccessories.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvAccessories.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvAccessories.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@AccessoriesActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_accessories)
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