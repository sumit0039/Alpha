package com.softwill.alpha.institute.culture.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.institute.culture.adapter.SponsorsAdapter
import com.softwill.alpha.institute.culture.model.CultureSponsorsModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SponsorsActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivitySponsorsBinding
    private var mDelayHandler: Handler? = null
    var mSponsorsAdapter: SponsorsAdapter? = null

    val mCultureSponsorsModel = java.util.ArrayList<CultureSponsorsModel>()


    var year = "2023"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_sponsors
        )



        setupBack()
        setSpinnerYear()
        setupSwipeListener()
        setupAdapter()
        apiCultureSponsers()
    }

    private fun setSpinnerYear() {
        val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.YearType,
            R.layout.simple_spinner_item
        )
        monthSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerYear.adapter = monthSpinnerAdapter
        binding.spinnerYear.setSelection(0)
        binding.spinnerYear.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                /* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                year = adapterView.getItemAtPosition(pos).toString()
                apiCultureSponsers()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {

        mSponsorsAdapter = SponsorsAdapter(this@SponsorsActivity, mCultureSponsorsModel)
        binding.rvSponsors.adapter = mSponsorsAdapter
        mSponsorsAdapter!!.notifyDataSetChanged()


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
        Toast.makeText(applicationContext, "Updated!!", Toast.LENGTH_SHORT).show()
    }

    private fun apiCultureSponsers() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SponsorsActivity).myApi.api_CultureSponsers(year)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CultureSponsorsModel>>() {}.type
                        val mList: List<CultureSponsorsModel> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mCultureSponsorsModel.clear()
                        mCultureSponsorsModel.addAll(mList)


                        if (mCultureSponsorsModel.isNotEmpty()) {
                            mSponsorsAdapter?.notifyDataSetChanged()
                            binding.rvSponsors.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvSponsors.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvSponsors.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@SponsorsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }




    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_team_member)
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