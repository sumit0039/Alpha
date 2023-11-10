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
import com.softwill.alpha.institute.sport.adapter.TeamSportAdapter
import com.softwill.alpha.institute.sport.model.SportTeamMember
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamSportActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityTeamSportBinding
    private var mDelayHandler: Handler? = null
    var mTeamSportAdapter: TeamSportAdapter? = null

    val mSportTeamMember = java.util.ArrayList<SportTeamMember>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_team_sport
        )


        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiSportTeamMembers()

    }

    private fun setupAdapter() {


        mTeamSportAdapter = TeamSportAdapter(this@TeamSportActivity, mSportTeamMember)
        binding.rvTeam.adapter = mTeamSportAdapter
        mTeamSportAdapter!!.notifyDataSetChanged()


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
        apiSportTeamMembers()
    }


    private fun apiSportTeamMembers() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeamSportActivity).myApi.api_SportTeamMembers()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SportTeamMember>>() {}.type
                        val mList: List<SportTeamMember> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mSportTeamMember.clear()
                        mSportTeamMember.addAll(mList)


                        if (mSportTeamMember.isNotEmpty()) {
                            mTeamSportAdapter?.notifyDataSetChanged()
                            binding.rvTeam.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvTeam.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTeam.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TeamSportActivity)
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