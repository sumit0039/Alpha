package com.softwill.alpha.home.activityimport android.annotation.SuppressLintimport android.app.SearchManagerimport android.content.Contextimport android.os.Bundleimport android.view.Menuimport android.view.MenuItemimport android.view.Viewimport androidx.appcompat.app.ActionBarimport androidx.appcompat.app.AppCompatActivityimport androidx.appcompat.widget.SearchViewimport androidx.core.content.ContextCompatimport androidx.databinding.DataBindingUtilimport com.google.gson.Gsonimport com.softwill.alpha.Rimport com.softwill.alpha.databinding.ActivitySearchHomeBindingimport com.softwill.alpha.home.adapter.SearchAdapterimport com.softwill.alpha.home.model.SearchResponseimport com.softwill.alpha.networking.RetrofitClientimport com.softwill.alpha.utils.UtilsFunctionsimport okhttp3.ResponseBodyimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass SearchHomeActivity : AppCompatActivity(), View.OnClickListener {    private lateinit var binding: ActivitySearchHomeBinding    var mSearchAdapter: SearchAdapter? = null    var mSearchType: String = "All"    private val searchResponse: ArrayList<SearchResponse> = ArrayList()    private val filterResponse: ArrayList<SearchResponse> = ArrayList()    private var mSearchText: String = ""    @SuppressLint("NotifyDataSetChanged")    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        binding =            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_search_home)        setupBack()        binding.tvAll.setOnClickListener(this)        binding.tvStudent.setOnClickListener(this)        binding.tvInstitute.setOnClickListener(this)        mSearchAdapter = SearchAdapter(searchResponse, this.applicationContext)        binding.rvAll.adapter = mSearchAdapter        mSearchAdapter!!.notifyDataSetChanged()    }    private fun setupBack() {        val actionBar: ActionBar? = supportActionBar        actionBar?.setDisplayShowTitleEnabled(false)        actionBar?.setDisplayHomeAsUpEnabled(true);        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)        actionBar?.setDisplayUseLogoEnabled(true);    }    override fun onCreateOptionsMenu(menu: Menu?): Boolean {        menuInflater.inflate(R.menu.search_menu2, menu)        val searchItem: MenuItem? = menu?.findItem(R.id.menu_search2)        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager        val searchView: SearchView = searchItem?.actionView as SearchView        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {            override fun onQueryTextChange(newText: String): Boolean {                if (newText.length >= 3) {                    mSearchText = newText                    apiSearch()                } else {                    searchResponse.clear()                    binding.rvAll.visibility = View.GONE                    binding.ivNoData.visibility = View.VISIBLE                }                return false            }            override fun onQueryTextSubmit(query: String): Boolean {                return false            }        })        return super.onCreateOptionsMenu(menu)    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        when (item.itemId) {            android.R.id.home -> {                finish()                return true            }        }        return super.onOptionsItemSelected(item)    }    override fun onClick(view: View?) {        when (view?.id) {            R.id.tvAll -> {                binding.tvAll.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.primary_color                    )                )                binding.tvStudent.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvInstitute.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_7_selected)                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_3)                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_3)                mSearchType = "All"                setAdapter(mSearchType)            }            R.id.tvStudent -> {                binding.tvAll.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvStudent.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.primary_color                    )                )                binding.tvInstitute.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_3)                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_7_selected)                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_3)                mSearchType = "Student"                setAdapter(mSearchType)            }            R.id.tvInstitute -> {                binding.tvAll.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvStudent.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.gray_color                    )                )                binding.tvInstitute.setTextColor(                    ContextCompat.getColor(                        applicationContext,                        R.color.primary_color                    )                )                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_3)                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_3)                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_7_selected)                mSearchType = "Institute"                setAdapter(mSearchType)            }        }    }    private fun apiSearch() {        val call: Call<ResponseBody> =            RetrofitClient.getInstance(this@SearchHomeActivity).myApi.api_Search(                mSearchType,                mSearchText            )        call.enqueue(object : Callback<ResponseBody> {            @SuppressLint("NotifyDataSetChanged")            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {                if (response.isSuccessful) {                    val responseJson = response.body()?.string()                    if (!responseJson.isNullOrEmpty()) {                        searchResponse.clear()                        searchResponse.addAll(                            Gson().fromJson(responseJson, Array<SearchResponse>::class.java)                                .toList()                        )                        setAdapter(mSearchType)                    } else {                        binding.rvAll.visibility = View.GONE                        binding.ivNoData.visibility = View.VISIBLE                    }                } else {                    binding.rvAll.visibility = View.GONE                    binding.ivNoData.visibility = View.VISIBLE                    UtilsFunctions().handleErrorResponse(response, this@SearchHomeActivity);                }            }            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {                t.printStackTrace()            }        })    }    @SuppressLint("NotifyDataSetChanged")    private fun setAdapter(mSearchType: String) {        filterResponse.clear()        for (data in searchResponse) {            when (mSearchType) {                "All" -> filterResponse.add(data)                "Student" -> {                    if (data.userTypeId == 2 || data.userTypeId == 3) {                        filterResponse.add(data)                    }                }                "Institute" -> {                    if (data.userTypeId == 1) {                        filterResponse.add(data)                    }                }            }        }        binding.rvAll.visibility = if (filterResponse.isNotEmpty()) View.VISIBLE else View.GONE        binding.ivNoData.visibility = if (filterResponse.isNotEmpty()) View.GONE else View.VISIBLE        mSearchAdapter?.notifyDataSetChanged()    }}