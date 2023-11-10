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
import com.softwill.alpha.databinding.ActivitySearchLibraryBinding
import com.softwill.alpha.institute.library.Model.Book
import com.softwill.alpha.institute.library.adapter.LibraryAllBookAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchLibraryBinding
    var mLibraryAllBookAdapter: LibraryAllBookAdapter? = null

    private val mAllBookList: ArrayList<Book> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_library
            )


        setupBack()
        setupAdapter()

    }

    private fun setupAdapter() {

        mLibraryAllBookAdapter = LibraryAllBookAdapter(this, mAllBookList)
        binding.rvSearchLibrary.adapter = mLibraryAllBookAdapter
        mLibraryAllBookAdapter!!.notifyDataSetChanged()

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)
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
                    apiSearchBook(newText)
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvSearchLibrary.visibility = View.GONE

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


    private fun apiSearchBook(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SearchLibraryActivity).myApi.api_SearchBook(
                searchText,
                0,
                1000
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<Book>>() {}.type
                        val mList: List<Book> = Gson().fromJson(responseBody, listType)

                        mAllBookList.clear()
                        mAllBookList.addAll(mList)


                        if (mAllBookList.isNotEmpty()) {
                            mLibraryAllBookAdapter?.notifyDataSetChanged()
                            binding.rvSearchLibrary.visibility = View.VISIBLE
                            binding.tvResults.visibility = View.VISIBLE

                        } else {
                            binding.rvSearchLibrary.visibility = View.GONE
                            binding.tvResults.visibility = View.GONE

                        }
                    }
                } else {
                    binding.rvSearchLibrary.visibility = View.GONE
                    binding.tvResults.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@SearchLibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}