package com.softwill.alpha.career.mack_exam.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.career.mack_exam.adapter.MockExamResultAdapter
import com.softwill.alpha.career.mack_exam.model.ExamResultModel
import com.softwill.alpha.databinding.ActivitySearchResultMockExamBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultMockExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultMockExamBinding
    var mMockExamResultAdapter: MockExamResultAdapter? = null

    val mExamResultModel = java.util.ArrayList<ExamResultModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_result_mock_exam
            )


        setupBack()
        setupAdapter()
    }

    private fun setupAdapter() {
        mMockExamResultAdapter = MockExamResultAdapter(this@SearchResultMockExamActivity, mExamResultModel)
        binding.rvSearchMockExamResult.adapter = mMockExamResultAdapter
        mMockExamResultAdapter?.notifyDataSetChanged()

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


    }

    private fun apiSearchCompletedMockExam(searchText: String) {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SearchResultMockExamActivity)
            .myApi.api_SearchCompletedMockExam(searchText, "0", "1000")

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
                            binding.rvSearchMockExamResult.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mMockExamResultAdapter?.notifyDataSetChanged()
                        } else {
                            binding.rvSearchMockExamResult.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.rvSearchMockExamResult.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SearchResultMockExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.search_menu2, menu)
        val searchItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_search2)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.isIconified = false;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 3) {
                    apiSearchCompletedMockExam(newText)
                } else {
                    binding.tvExam.visibility = View.GONE
                    binding.rvSearchMockExamResult.visibility = View.GONE
                    binding.llNoData.visibility = View.VISIBLE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })






        return super.onCreateOptionsMenu(menu)
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