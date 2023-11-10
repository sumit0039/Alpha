package com.softwill.alpha.career.career_guidance.activity

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
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.adapter.CareerGuidanceAdapter
import com.softwill.alpha.career.career_guidance.model.CareerGuidanceModel
import com.softwill.alpha.databinding.ActivitySearchCareerGuidanceBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchCareerGuidanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchCareerGuidanceBinding
    var mCareerGuidanceAdapter: CareerGuidanceAdapter? = null
    private val mCareerGuidanceList = ArrayList<CareerGuidanceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_career_guidance
            )

        setupBack()
        setupAdapter()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {


        mCareerGuidanceAdapter = CareerGuidanceAdapter(mCareerGuidanceList, this)
        binding.rvCareerGuidanceSearch.adapter = mCareerGuidanceAdapter
        mCareerGuidanceAdapter!!.notifyDataSetChanged()
    }

    private fun apiSearchCareerGuidance(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SearchCareerGuidanceActivity).myApi.api_SearchCareerGuidance(
                searchText,
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val jsonArray = JSONArray(responseJson)
                        // val careerGuidanceList = mutableListOf<CareerGuidanceModel>()
                        mCareerGuidanceList.clear()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val id = jsonObject.getInt("id")
                            val facultyName = jsonObject.getString("facultyName")
                            val streamName = jsonObject.getString("streamName")

                            val careerGuidanceModel =
                                CareerGuidanceModel(id, facultyName, streamName)
                            mCareerGuidanceList.add(careerGuidanceModel)
                        }


                        if (mCareerGuidanceList.isNotEmpty()) {
                            binding.tvResults.visibility = View.VISIBLE
                            binding.rvCareerGuidanceSearch.visibility = View.VISIBLE
                            binding.rlNoData.visibility = View.GONE
                            mCareerGuidanceAdapter?.notifyDataSetChanged()

                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvCareerGuidanceSearch.visibility = View.GONE
                            binding.rlNoData.visibility = View.VISIBLE
                        }

                    } else {
                        binding.tvResults.visibility = View.GONE
                        binding.rvCareerGuidanceSearch.visibility = View.GONE
                        binding.rlNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(
                        response,
                        this@SearchCareerGuidanceActivity
                    )
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
                    apiSearchCareerGuidance(newText)
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvCareerGuidanceSearch.visibility = View.GONE
                    binding.rlNoData.visibility = View.VISIBLE
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