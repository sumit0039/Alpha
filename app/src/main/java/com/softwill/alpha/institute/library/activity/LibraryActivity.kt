package com.softwill.alpha.institute.library.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityLibraryBinding
import com.softwill.alpha.institute.library.Model.Book
import com.softwill.alpha.institute.library.Model.BookCategories
import com.softwill.alpha.institute.library.Model.NewsPaper
import com.softwill.alpha.institute.library.Model.NewsPaperCategories
import com.softwill.alpha.institute.library.adapter.*
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryActivity : AppCompatActivity(), BookCategoryAdapter.BookCategoryCallbackInterface,
    NewsPaperCategoryAdapter.NewsPaperCategoryCallbackInterface, View.OnClickListener {

    private lateinit var binding: ActivityLibraryBinding
    var mCategoryWiseBookAdapter: CategoryWiseBookAdapter? = null
    var mNewspaperAdapter: NewspaperAdapter? = null
    var mLibraryRecentAdapter: LibraryRecentAdapter? = null
    var mLibraryAllBookAdapter: LibraryAllBookAdapter? = null
    var mBookCategoryAdapter: BookCategoryAdapter? = null
    var mNewsPaperCategoryAdapter: NewsPaperCategoryAdapter? = null

    private val mNewsPaperCategories: ArrayList<NewsPaperCategories> = ArrayList()
    private val mNewsPaper: ArrayList<NewsPaper> = ArrayList()

    private val mBookCategories: ArrayList<BookCategories> = ArrayList()
    private val mRecentBookList: ArrayList<Book> = ArrayList()
    private val mAllBookList: ArrayList<Book> = ArrayList()
    private val mCategoryWiseBookList: ArrayList<Book> = ArrayList()


    private var dialog: BottomSheetDialog? = null
    var newClickPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_library)


        setupBack()
        setupAdapter()

        binding.rlFilterNewspaper.setOnClickListener(this)

        //API CALL
        apiBookCategories()
        apiRecentBook()
        apiAllBook()
        apiNewsPaperCategories()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {
        //Book Category
        mBookCategoryAdapter = BookCategoryAdapter(mBookCategories, this, this)
        binding.rvBookCategory.adapter = mBookCategoryAdapter
        mBookCategoryAdapter!!.notifyDataSetChanged()


        //Recent Book
        mLibraryRecentAdapter = LibraryRecentAdapter(this, mRecentBookList)
        binding.rvRecentBook.adapter = mLibraryRecentAdapter
        mLibraryRecentAdapter!!.notifyDataSetChanged()

        //All Book
        mLibraryAllBookAdapter = LibraryAllBookAdapter(this, mAllBookList)
        binding.rvAllBook.adapter = mLibraryAllBookAdapter
        mLibraryAllBookAdapter!!.notifyDataSetChanged()

        //NewsPaper
        mNewspaperAdapter = NewspaperAdapter(this, mNewsPaper)
        binding.rvNewspaper.adapter = mNewspaperAdapter
        mNewspaperAdapter!!.notifyDataSetChanged()

        //Category Wise Book
        mCategoryWiseBookAdapter = CategoryWiseBookAdapter(this, mCategoryWiseBookList)
        binding.rvCategoryWiseBook.adapter = mCategoryWiseBookAdapter
        mCategoryWiseBookAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlFilterNewspaper -> {
                addFilterBottomSheet()
            }
        }
    }

    private fun addFilterBottomSheet() {
        dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_filter_newspaper, null)


        val rvNewspaperCategory = view.findViewById<RecyclerView>(R.id.rvNewspaperCategory)


        mNewsPaperCategoryAdapter = NewsPaperCategoryAdapter(
            mNewsPaperCategories,
            this,
            this,
        )

        mNewsPaperCategoryAdapter!!.updateClickPosition(newClickPosition)
        rvNewspaperCategory.adapter = mNewsPaperCategoryAdapter
        mNewsPaperCategoryAdapter?.notifyDataSetChanged()


        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(view)
        dialog!!.show()
    }

    override fun newsPaperCategoryClickCallback(categoryId: Int, position: Int) {
        newClickPosition = position
        dialog?.dismiss()
        binding.tvNewsPaperCategory.text = mNewsPaperCategories[position].name
        apiNewsPaper(categoryId)
    }


    @SuppressLint("SoonBlockedPrivateApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.lib_menu, menu)
        /* val searchItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_search)
         val saveItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_save)


         val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
         val searchView: SearchView = searchItem?.actionView as SearchView
         searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
         searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

             override fun onQueryTextChange(newText: String): Boolean {
                 //filter(newText)
                 return false
             }

             override fun onQueryTextSubmit(query: String): Boolean {
                 return false
             }

         })*/



        return super.onCreateOptionsMenu(menu)
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_library)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_search -> {
                val intent = Intent(applicationContext, SearchLibraryActivity::class.java)
                startActivity(intent)
                return true
            }
            com.softwill.alpha.R.id.menu_save -> {
                val intent = Intent(applicationContext, SavedActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun itemClickCallback(categoryId: Int, position: Int) {

        //All
        if (categoryId == -1) {
            apiRecentBook()
            apiAllBook()

            binding.LLAll.visibility = View.VISIBLE
            binding.LLNewsPaper.visibility = View.GONE
            binding.LLStoryBooks.visibility = View.GONE
        }
        //NewsPaper
        else if (categoryId == 0) {
            binding.LLAll.visibility = View.GONE
            binding.LLNewsPaper.visibility = View.VISIBLE
            binding.LLStoryBooks.visibility = View.GONE
        } else {
            apiCategoryWiseBook(categoryId)

            binding.LLAll.visibility = View.GONE
            binding.LLNewsPaper.visibility = View.GONE
            binding.LLStoryBooks.visibility = View.VISIBLE
        }


    }

    private fun apiBookCategories() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_BookCategories()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<BookCategories>>() {}.type
                        val mList: List<BookCategories> = Gson().fromJson(responseBody, listType)

                        mBookCategories.clear()
                        mBookCategories.add(BookCategories(-1, "All"))
                        mBookCategories.add(BookCategories(0, "News Paper"))
                        mBookCategories.addAll(mList)


                        if (mBookCategories.isNotEmpty()) {
                            mBookCategoryAdapter?.notifyDataSetChanged()
                            binding.rvBookCategory.visibility = View.VISIBLE
                        } else {
                            binding.rvBookCategory.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvBookCategory.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiRecentBook() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_RecentBook(true, 0, 100)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<Book>>() {}.type
                        val mList: List<Book> = Gson().fromJson(responseBody, listType)

                        mRecentBookList.clear()
                        mRecentBookList.addAll(mList)


                        if (mRecentBookList.isNotEmpty()) {
                            mLibraryRecentAdapter?.notifyDataSetChanged()
                            binding.rvRecentBook.visibility = View.VISIBLE
                            binding.tvRecentBook.visibility = View.VISIBLE
                        } else {
                            binding.rvRecentBook.visibility = View.GONE
                            binding.tvRecentBook.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rvRecentBook.visibility = View.GONE
                    binding.tvRecentBook.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiAllBook() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_AllBook(0, 1000)

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
                            binding.rvAllBook.visibility = View.VISIBLE

                        } else {
                            binding.rvRecentBook.visibility = View.GONE

                        }
                    }
                } else {
                    binding.rvRecentBook.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiCategoryWiseBook(categoryId: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_CategoryWiseBook(
                categoryId,
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

                        mCategoryWiseBookList.clear()
                        mCategoryWiseBookList.addAll(mList)


                        if (mCategoryWiseBookList.isNotEmpty()) {
                            mCategoryWiseBookAdapter?.notifyDataSetChanged()
                            binding.rvCategoryWiseBook.visibility = View.VISIBLE

                        } else {
                            binding.rvCategoryWiseBook.visibility = View.GONE

                        }
                    }
                } else {
                    binding.rvCategoryWiseBook.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiNewsPaperCategories() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_NewsPaperCategories()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<NewsPaperCategories>>() {}.type
                        val mList: List<NewsPaperCategories> =
                            Gson().fromJson(responseBody, listType)

                        mNewsPaperCategories.clear()
                        mNewsPaperCategories.addAll(mList)

                        if (mNewsPaperCategories.isNotEmpty()) {
                            binding.tvNewsPaperCategory.text = mNewsPaperCategories[0].name
                            apiNewsPaper(mNewsPaperCategories[0].categoryId)
                        }

                    }
                } else {
                    binding.rvBookCategory.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiNewsPaper(categoryId: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@LibraryActivity).myApi.api_NewsPaper(categoryId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<NewsPaper>>() {}.type
                        val mList: List<NewsPaper> = Gson().fromJson(responseBody, listType)

                        mNewsPaper.clear()
                        mNewsPaper.addAll(mList)


                        if (mNewsPaper.isNotEmpty()) {
                            mNewspaperAdapter?.notifyDataSetChanged()
                            binding.rvNewspaper.visibility = View.VISIBLE

                        } else {
                            binding.rvNewspaper.visibility = View.GONE

                        }
                    }
                } else {
                    binding.rvNewspaper.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, this@LibraryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        apiRecentBook()
        apiAllBook()

    }


}