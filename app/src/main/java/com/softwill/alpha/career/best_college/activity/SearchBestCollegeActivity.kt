package com.softwill.alpha.career.best_college.activity

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
import com.softwill.alpha.career.best_college.adapter.BestCollegeAdapter
import com.softwill.alpha.career.best_college.model.BestCollegeModel
import com.softwill.alpha.databinding.ActivitySearchBestCollegeBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchBestCollegeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBestCollegeBinding
    var mBestCollegeAdapter: BestCollegeAdapter? = null

    private val mBestCollegeList = ArrayList<BestCollegeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_best_college
            )

        setupBack()
        setupAdapter()


    }

    private fun setupAdapter() {
      //  list.add(BestCollegeItemModel("Institute of Engineering and Technology", true))
      //  list.add(BestCollegeItemModel("Indian Institute of Technology", false))
      //  list.add(BestCollegeItemModel("Acropolis Institute of Technology & Research", false))
      //  list.add(BestCollegeItemModel("Shri Govindram Seksaria Institute of Technology and Science", true))


        mBestCollegeAdapter = BestCollegeAdapter(mBestCollegeList, this@SearchBestCollegeActivity)
        binding.rvBestCollegeSearch.adapter = mBestCollegeAdapter
        mBestCollegeAdapter!!.notifyDataSetChanged()
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
                    apiSearchBestCollegeList(newText)
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvBestCollegeSearch.visibility = View.GONE
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


    private fun apiSearchBestCollegeList(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SearchBestCollegeActivity).myApi.api_SearchBestCollegeList(
                searchText
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<BestCollegeModel>>() {}.type
                        val mList: List<BestCollegeModel> = Gson().fromJson(responseBody, listType)

                        // Update the mBestCollegeList list with the new data
                        mBestCollegeList.clear()
                        mBestCollegeList.addAll(mList)


                        if (mBestCollegeList.isNotEmpty()) {

                            mBestCollegeAdapter?.notifyDataSetChanged()

                            binding.tvResults.visibility = View.VISIBLE
                            binding.rvBestCollegeSearch.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE


                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvBestCollegeSearch.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvBestCollegeSearch.visibility = View.GONE
                    binding.llNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@SearchBestCollegeActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}

