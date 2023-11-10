package com.softwill.alpha.career.entrance_exam.activity

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
import com.softwill.alpha.R
import com.softwill.alpha.career.entrance_exam.adapter.EntranceExamResultAdapter
import com.softwill.alpha.career.entrance_exam.model.EntranceExamModel
import com.softwill.alpha.databinding.ActivitySearchEntranceExamBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchEntranceExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchEntranceExamBinding
    val mEntranceExamList = java.util.ArrayList<EntranceExamModel>()
    var mEntranceExamResultAdapter: EntranceExamResultAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_entrance_exam
            )


        setupBack()
        setupAdapter()
    }

    private fun setupAdapter() {
        mEntranceExamResultAdapter =
            EntranceExamResultAdapter(mEntranceExamList, applicationContext)
        binding.rvEntranceExamSearch.adapter = mEntranceExamResultAdapter
        mEntranceExamResultAdapter?.notifyDataSetChanged()
    }

    private fun apiSearchEntranceExam(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SearchEntranceExamActivity).myApi.api_SearchEntranceExam(
                searchText
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {

                        val listType = object : TypeToken<List<EntranceExamModel>>() {}.type
                        val mList: List<EntranceExamModel> = Gson().fromJson(responseJson, listType)

                        mEntranceExamList.clear()
                        mEntranceExamList.addAll(mList)

                        if (mEntranceExamList.isNotEmpty()) {
                            binding.tvResults.visibility = View.VISIBLE
                            binding.rvEntranceExamSearch.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mEntranceExamResultAdapter?.notifyDataSetChanged()
                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvEntranceExamSearch.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.tvResults.visibility = View.GONE
                        binding.rvEntranceExamSearch.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SearchEntranceExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);
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
                    apiSearchEntranceExam(newText)
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvEntranceExamSearch.visibility = View.GONE
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