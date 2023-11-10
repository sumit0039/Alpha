package com.softwill.alpha.career.mack_exam.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.adapter.MockExamResultAdapter
import com.softwill.alpha.career.mack_exam.model.ExamResultModel
import com.softwill.alpha.databinding.ActivityMockExamResultBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockExamResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMockExamResultBinding
    private var mDelayHandler: Handler? = null
    var mMockExamResultAdapter: MockExamResultAdapter? = null

    val mExamResultModel = java.util.ArrayList<ExamResultModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_mock_exam_result
        )


        setupBack()
        setupSwipeListener()
        setupMockExamResultAdapter()
        apiMockExamCompleted()
    }


    private fun setupMockExamResultAdapter() {

        mMockExamResultAdapter = MockExamResultAdapter(this@MockExamResultActivity, mExamResultModel)
        binding.rvMockExamResult.adapter = mMockExamResultAdapter
        mMockExamResultAdapter?.notifyDataSetChanged()
    }


    private fun apiMockExamCompleted() {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@MockExamResultActivity)
            .myApi.api_MockExamCompleted()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<ExamResultModel>>() {}.type
                        val mList: List<ExamResultModel> = Gson().fromJson(responseJson, listType)

                        // Update your mEntranceExamList with the new data
                        mExamResultModel.clear()
                        mExamResultModel.addAll(mList)

                        if (mExamResultModel.isNotEmpty()) {
                            binding.rvMockExamResult.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mMockExamResultAdapter?.notifyDataSetChanged()
                        } else {
                            binding.rvMockExamResult.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.rvMockExamResult.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@MockExamResultActivity)
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
        apiMockExamCompleted()
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.result)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_search -> {
                val intent =
                    Intent(this@MockExamResultActivity, SearchResultMockExamActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.search_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }


}