package com.softwill.alpha.profile.privacy.blockedPeople

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityBlockedPeopleBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockedPeopleActivity : AppCompatActivity(), BlockedPeopleAdapter.CallbackInterface {

    private lateinit var binding: ActivityBlockedPeopleBinding
    var mBlockedPeopleAdapter: BlockedPeopleAdapter? = null
    val mBlockedUserResponse = ArrayList<BlockedUserResponse>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blocked_people)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_blocked_people)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)


        mBlockedPeopleAdapter = BlockedPeopleAdapter(mBlockedUserResponse, applicationContext, this)
        binding.rvBlockedPeople.adapter = mBlockedPeopleAdapter
        mBlockedPeopleAdapter!!.notifyDataSetChanged()


        apiBlockUserList()
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


    override fun passResultCallback(position: Int, blockUserId: Int) {
        apiUnBlockUser(blockUserId)
        //mBlockedPeopleAdapter?.removeItem(position)
        //binding.tvListSize.text = mBlockedUserResponse.size.toString().padStart(2, '0')
    }

    private fun apiUnBlockUser(blockUserId: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("userId", blockUserId)
            addProperty("status", "unblock")
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@BlockedPeopleActivity).myApi.api_BlockUnblockUser(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Unblocked successfully") {
                            apiBlockUserList()
                        }

                        UtilsFunctions().showToast(
                            this@BlockedPeopleActivity,
                            responseObject.getString("message")
                        )


                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@BlockedPeopleActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@BlockedPeopleActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiBlockUserList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@BlockedPeopleActivity).myApi.api_BlockUserList()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    val responseJson = response.body()?.string()
                    val blockUsers = UtilsFunctions().parseBlockUserList(responseJson)

                    if (blockUsers != null) {
                        mBlockedUserResponse.clear()
                        mBlockedUserResponse.addAll(blockUsers)
                        binding.tvListSize.text =
                            mBlockedUserResponse.size.toString().padStart(2, '0')
                        mBlockedPeopleAdapter?.notifyDataSetChanged()
                    } else {
                        // Handle parsing error
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@BlockedPeopleActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}