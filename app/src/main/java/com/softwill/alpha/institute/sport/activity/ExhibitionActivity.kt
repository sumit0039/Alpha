package com.softwill.alpha.institute.sport.activity

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
import com.softwill.alpha.databinding.ActivityExhibitionBinding
import com.softwill.alpha.institute.sport.adapter.ExhibitionAdapter
import com.softwill.alpha.institute.sport.model.SportExhibitions
import com.softwill.alpha.institute.sport.model.SportTeamMember
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExhibitionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityExhibitionBinding
    private var mDelayHandler: Handler? = null
    var mExhibitionAdapter: ExhibitionAdapter? = null

    var year = "2023"

    val mSportExhibitions = java.util.ArrayList<SportExhibitions>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_exhibition)


        setupBack()
        setSpinnerYear()
        setupSwipeListener()
        setupAdapter()
        apiSportExhibitions()

    }

    private fun setupAdapter() {


        mExhibitionAdapter = ExhibitionAdapter(this@ExhibitionActivity, mSportExhibitions)
        binding.rvExhibition.adapter = mExhibitionAdapter
        mExhibitionAdapter!!.notifyDataSetChanged()


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
        apiSportExhibitions()
    }

    private fun apiSportExhibitions() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ExhibitionActivity).myApi.api_SportExhibitions( year)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SportExhibitions>>() {}.type
                        val mList: List<SportExhibitions> = Gson().fromJson(responseBody, listType)

                        mSportExhibitions.clear()
                        mSportExhibitions.addAll(mList)


                        if (mSportExhibitions.isNotEmpty()) {
                            mExhibitionAdapter?.notifyDataSetChanged()
                            binding.rvExhibition.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvExhibition.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvExhibition.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@ExhibitionActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
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

                apiSportExhibitions()

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }



    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_exhibition)
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