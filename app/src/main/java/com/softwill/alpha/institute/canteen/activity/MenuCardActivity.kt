package com.softwill.alpha.institute.canteen.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityMenuCardBinding
import com.softwill.alpha.institute.canteen.adapter.MealAdapter
import com.softwill.alpha.institute.canteen.adapter.SnackAdapter
import com.softwill.alpha.institute.canteen.model.CanteenMenuCard
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuCardBinding
    private var mDelayHandler: Handler? = null
    var mSnackAdapter: SnackAdapter? = null
    var mMealAdapter: MealAdapter? = null

    val mCanteenMenuCard = java.util.ArrayList<CanteenMenuCard>()
    private val mCanteenMeal = java.util.ArrayList<CanteenMenuCard>()
    private val mCanteenSnacks = java.util.ArrayList<CanteenMenuCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_menu_card)


        setupBack()
        onClickListener()
        setupSwipeListener()
        setupAdapter()
        apiCanteenMenuCard()
    }

    private fun setupAdapter() {

        mSnackAdapter = SnackAdapter(mCanteenSnacks, applicationContext)
        binding.rvSnack.adapter = mSnackAdapter
        mSnackAdapter!!.notifyDataSetChanged()


        mMealAdapter = MealAdapter(mCanteenMeal, applicationContext)
        binding.rvMeal.adapter = mMealAdapter
        mMealAdapter!!.notifyDataSetChanged()
    }


    private fun apiCanteenMenuCard() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@MenuCardActivity).myApi.api_CanteenMenuCard()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CanteenMenuCard>>() {}.type
                        val mList: List<CanteenMenuCard> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mCanteenMenuCard.clear()
                        mCanteenMenuCard.addAll(mList)


                        if (mCanteenMenuCard.isNotEmpty()) {

                            val (snacks, meals) = mCanteenMenuCard.partition { it.itemType == 1 }

                            mCanteenSnacks.addAll(snacks)
                            mCanteenMeal.addAll(meals)



                            if (mCanteenSnacks.isNotEmpty()) {
                                mSnackAdapter?.notifyDataSetChanged()
                                binding.rvSnack.visibility = View.VISIBLE
                            } else {
                                binding.rvSnack.visibility = View.GONE
                            }


                            if (mCanteenMeal.isNotEmpty()) {
                                mMealAdapter?.notifyDataSetChanged()
                                binding.rvMeal.visibility = View.VISIBLE
                            } else {
                                binding.rvMeal.visibility = View.GONE
                            }


                        } else {
                            binding.rvSnack.visibility = View.GONE
                            binding.rvMeal.visibility = View.GONE

                        }
                    }
                } else {
                    binding.rvSnack.visibility = View.GONE
                    binding.rvMeal.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@MenuCardActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
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
        apiCanteenMenuCard()
    }


    private fun onClickListener() {
        binding.tvSnacks.setOnClickListener {
            binding.tvSnacks.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.tvSnacks.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvMeal.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvMeal.setBackgroundResource(R.drawable.bg_rounded_3)

            binding.rvMeal.visibility = View.GONE


            if (mCanteenSnacks.isNotEmpty()) {
                binding.rvSnack.visibility = View.VISIBLE

            } else {
                binding.rvSnack.visibility = View.GONE

            }

        }

        binding.tvMeal.setOnClickListener {
            binding.tvMeal.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            binding.tvMeal.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvSnacks.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            binding.tvSnacks.setBackgroundResource(R.drawable.bg_rounded_3)

            binding.rvSnack.visibility = View.GONE


            if (mCanteenMeal.isNotEmpty()) {
                binding.rvMeal.visibility = View.VISIBLE
            } else {
                binding.rvMeal.visibility = View.GONE
            }


        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_menu_card)
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