package com.softwill.alpha.institute.culture.activity

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
import com.softwill.alpha.institute.culture.adapter.CalendarEventAdapter
import com.softwill.alpha.institute.culture.model.CultureCalendarProgram
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityCalendarBinding
    var calendar: Calendar? = null
    var mCalendarEventAdapter: CalendarEventAdapter? = null

    val mCultureCalendarProgram = java.util.ArrayList<CultureCalendarProgram>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_calendar
        )


        setupBack()
        setCalendarView()
        setupAdapter()
        apiCultureProgram("2023-08-15")
    }

    private fun setupAdapter() {

        mCalendarEventAdapter = CalendarEventAdapter(this@CalendarActivity, mCultureCalendarProgram)
        binding.rvCalendarEvent.adapter = mCalendarEventAdapter
        mCalendarEventAdapter!!.notifyDataSetChanged()

    }

    private fun setCalendarView() {
        calendar = Calendar.getInstance()
        //calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        //calendar.set(Calendar.DAY_OF_MONTH, 9);
        //calendar.set(Calendar.YEAR, 2012);
        //calendar.add(Calendar.DAY_OF_MONTH, 1);
        //calendar.add(Calendar.YEAR, 1);
        binding.calendarView
            .setOnDateChangeListener { _, year, month, dayOfMonth ->
                val date = (year.toString() + "-" + (month + 1) + "-" + dayOfMonth.toString())
                apiCultureProgram(date)

                /* if(date == "15-02-2023"){
                     binding.rvCalendarEvent.visibility = View.VISIBLE
                     binding.tvNoData.visibility = View.GONE
                 }else{
                     binding.rvCalendarEvent.visibility = View.GONE
                     binding.tvNoData.visibility = View.VISIBLE
                 }
 */
                // Toast.makeText(applicationContext, date, Toast.LENGTH_SHORT).show();

            }


    }

    private fun apiCultureProgram(date: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CalendarActivity).myApi.api_CultureProgram(date)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CultureCalendarProgram>>() {}.type
                        val mList: List<CultureCalendarProgram> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mCultureCalendarProgram.clear()
                        mCultureCalendarProgram.addAll(mList)


                        if (mCultureCalendarProgram.isNotEmpty()) {
                            mCalendarEventAdapter?.notifyDataSetChanged()
                            binding.rvCalendarEvent.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvCalendarEvent.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvCalendarEvent.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@CalendarActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_calendar)
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