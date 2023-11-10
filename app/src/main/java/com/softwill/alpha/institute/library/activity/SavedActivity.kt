package com.softwill.alpha.institute.library.activity

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
import com.softwill.alpha.databinding.ActivitySavedBinding
import com.softwill.alpha.institute.library.Model.SavedBookCategory
import com.softwill.alpha.institute.library.adapter.SavedBookCategoryAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBinding

    var mSavedBookCategoryAdapter: SavedBookCategoryAdapter? = null

    private val mSavedBookCategory: ArrayList<SavedBookCategory> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_saved)

        setupBack()
        setupAdapter()

        //API CALL
        apiSavedBook("")
    }


    private fun setupAdapter() {
        mSavedBookCategoryAdapter = SavedBookCategoryAdapter(this@SavedActivity, mSavedBookCategory)
        binding.rvSavedCategory.adapter = mSavedBookCategoryAdapter
        mSavedBookCategoryAdapter!!.notifyDataSetChanged()



    }

    private fun apiSavedBook(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SavedActivity).myApi.api_SavedBook(searchText)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SavedBookCategory>>() {}.type
                        val mList: List<SavedBookCategory> = Gson().fromJson(responseBody, listType)

                        mSavedBookCategory.clear()
                        mSavedBookCategory.addAll(mList)


                        if (mSavedBookCategory.isNotEmpty()) {
                            mSavedBookCategoryAdapter?.notifyDataSetChanged()
                            binding.rvSavedCategory.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE

                        } else {
                            binding.rvSavedCategory.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvSavedCategory.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@SavedActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_saved)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.search_menu, menu)
        val searchItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_search)


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 3) {
                    apiSavedBook(newText)
                } else {
                    apiSavedBook("")
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
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}