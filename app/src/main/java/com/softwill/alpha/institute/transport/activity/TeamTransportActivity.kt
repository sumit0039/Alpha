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
import com.softwill.alpha.institute.transport.adapter.TeamTransportAdapter
import com.softwill.alpha.institute.transport.model.TransportTeamMember
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamTransportActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityTeamTransportBinding
    private var mDelayHandler: Handler? = null
    var mTeamTransportAdapter: TeamTransportAdapter? = null

    val mTransportTeamMember = java.util.ArrayList<TransportTeamMember>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_team_transport
        )


        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiTransportTeamMember()
    }

    private fun setupAdapter() {


        mTeamTransportAdapter = TeamTransportAdapter(this@TeamTransportActivity, mTransportTeamMember)
        binding.rvTeam.adapter = mTeamTransportAdapter
        mTeamTransportAdapter!!.notifyDataSetChanged()


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
        apiTransportTeamMember()
    }


    private fun apiTransportTeamMember() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeamTransportActivity).myApi.api_TransportTeamMember()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val teamMembersListType = object : TypeToken<List<TransportTeamMember>>() {}.type
                        val teamMembersList  : List<TransportTeamMember> = Gson().fromJson(responseBody, teamMembersListType)

                        // Update the mTransportTeamMember list with the new data
                        mTransportTeamMember.clear()
                        mTransportTeamMember.addAll(teamMembersList)


                        if (mTransportTeamMember.isNotEmpty()){
                            mTeamTransportAdapter?.notifyDataSetChanged()
                            binding.rvTeam.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        }else{
                            binding.rvTeam.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTeam.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TeamTransportActivity)
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
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}