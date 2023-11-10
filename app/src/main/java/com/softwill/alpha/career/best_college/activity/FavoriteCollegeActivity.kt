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
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.career.best_college.adapter.FavoriteCollegeAdapter
import com.softwill.alpha.career.best_college.model.BestCollegeModel
import com.softwill.alpha.databinding.ActivityFavoriteCollegeBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FavoriteCollegeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteCollegeBinding
    var mFavoriteCollegeAdapter: FavoriteCollegeAdapter? = null
    private val mBestCollegeList = ArrayList<BestCollegeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_college)
        setupBack()



        mFavoriteCollegeAdapter = FavoriteCollegeAdapter(mBestCollegeList, applicationContext)
        binding.rvFavoriteCollege.adapter = mFavoriteCollegeAdapter
        mFavoriteCollegeAdapter!!.notifyDataSetChanged()


        apiFavouriteBestCollege()

    }





    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_favorite_colleges)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.menu_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: androidx.appcompat.widget.SearchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
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


    private fun apiFavouriteBestCollege() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@FavoriteCollegeActivity).myApi.api_FavouriteBestCollege(
               true
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

                            mFavoriteCollegeAdapter?.notifyDataSetChanged()

                            binding.tvResults.visibility = View.VISIBLE
                            binding.rvFavoriteCollege.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE


                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvFavoriteCollege.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvFavoriteCollege.visibility = View.GONE
                    binding.llNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@FavoriteCollegeActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun filter(key: String) {
        val filteredList: java.util.ArrayList<BestCollegeModel> = java.util.ArrayList()
        for (item in mBestCollegeList) {
            if (item.instituteName.lowercase(Locale.ROOT).contains(key.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
            mFavoriteCollegeAdapter?.filterList(filteredList)
        }
    }

}