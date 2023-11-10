package com.softwill.alpha.career.best_college.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.career.best_college.adapter.BestCollegeAdapter
import com.softwill.alpha.career.best_college.adapter.BestCollegeCategoryAdapter
import com.softwill.alpha.career.best_college.model.BestCollegeModel
import com.softwill.alpha.career.best_college.model.StateModel
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.career.career_guidance.model.StreamModel2
import com.softwill.alpha.databinding.ActivityBestCollegeBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BestCollegeActivity : AppCompatActivity(), View.OnClickListener,
    BestCollegeCategoryAdapter.BestCollegeCategoryCallbackInterface {

    private lateinit var binding: ActivityBestCollegeBinding

    var mBestCollegeAdapter: BestCollegeAdapter? = null
    private val mBestCollegeList = ArrayList<BestCollegeModel>()


    private val mStreamList: ArrayList<StreamModel2> = ArrayList()
    var mBestCollegeCategoryAdapter: BestCollegeCategoryAdapter? = null


    val mStateModel = ArrayList<StateModel>()
    private var mStateId: Int = -1
    val mStatesNameList: ArrayList<String> = ArrayList()

    val mFacilitiesList = ArrayList<FacultyModel2>()
    val mFacilitiesNameList: ArrayList<String> = ArrayList()
    private var mFacultyId: Int = -1

    private var mStreamId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_best_college)
        setupBack()
        setSpinnerFaculty()

        apiStates()
        apiFaculties()


       // binding.btnView.setOnClickListener(this)



        mBestCollegeAdapter = BestCollegeAdapter(mBestCollegeList, this)
        binding.rvBestCollege.adapter = mBestCollegeAdapter
        mBestCollegeAdapter!!.notifyDataSetChanged()



        mBestCollegeCategoryAdapter = BestCollegeCategoryAdapter(mStreamList, this, this)
        binding.rvBestCollegeCategory.adapter = mBestCollegeCategoryAdapter
        mBestCollegeCategoryAdapter!!.notifyDataSetChanged()

    }




    private fun setSpinnerFaculty() {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, mFacilitiesNameList)
        adapter.setDropDownViewResource(com.softwill.alpha.R.layout.custom_spinner_dropdown_item)
        binding.spinnerFaculty.adapter = adapter
        binding.spinnerFaculty.setSelection(0)
        binding.spinnerFaculty.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                /* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                val value = adapterView.getItemAtPosition(pos).toString()

                mFacultyId = mFacilitiesList[pos].facultyId
                mStreamId = mFacilitiesList[pos].streams[0].streamId

                mStreamList.clear()
                for (j in 0 until mFacilitiesList[pos].streams.size) {
                    val stream = StreamModel2(
                        mFacilitiesList[pos].streams[j].streamId,
                        mFacilitiesList[pos].streams[j].streamName
                    )
                    mStreamList.add(stream)
                }

                if (mStreamList.isNotEmpty()) {
                    binding.rvBestCollegeCategory.visibility = View.VISIBLE
                    mBestCollegeCategoryAdapter?.setPosition()
                    // mBestCollegeCategoryAdapter?.notifyDataSetChanged()
                }else{
                    binding.rvBestCollegeCategory.visibility = View.GONE
                }

                apiBestCollegeList()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun setSpinnerState() {
        /*val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext,
            com.softwill.alpha.R.array.StateType,
            com.softwill.alpha.R.layout.simple_spinner_item
        )*/
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, mStatesNameList)
        adapter.setDropDownViewResource(com.softwill.alpha.R.layout.custom_spinner_dropdown_item)
        binding.spinnerState.adapter = adapter
        binding.spinnerState.setSelection(0)
        binding.spinnerState.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                /* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                val value = adapterView.getItemAtPosition(pos).toString()

                mStateId = mStateModel[pos].id


                if(mFacultyId != -1){
                    apiBestCollegeList()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(com.softwill.alpha.R.string.title_best_college)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true)


    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.fav_menu, menu)
        /*val searchItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_search)
        val favItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_fav)


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })*/



        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            com.softwill.alpha.R.id.menu_fav -> {
                val intent = Intent(applicationContext, FavoriteCollegeActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_search -> {
                val intent = Intent(applicationContext, SearchBestCollegeActivity::class.java)
                startActivity(intent)
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }


    private fun filter(key: String) {
        val filteredList: ArrayList<BestCollegeModel> = ArrayList()
        for (item in mBestCollegeList) {
            if (item.instituteName.lowercase(Locale.ROOT)
                    .contains(key.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
            mBestCollegeAdapter?.filterList(filteredList)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /*R.id.btnView -> {
                binding.tvResults.visibility = View.VISIBLE
                binding.rvBestCollege.visibility = View.VISIBLE
                binding.rvBestCollegeCategory.visibility = View.VISIBLE
                binding.llNoData.visibility = View.GONE
            }*/
        }
    }

    private fun apiStates() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@BestCollegeActivity).myApi.api_States()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        try {
                            val jsonArray = JSONArray(responseJson)
                            val stateList = mutableListOf<StateModel>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val id = jsonObject.getInt("id")
                                val name = jsonObject.getString("name")
                                val state = StateModel(id, name)
                                stateList.add(state)
                            }

                            mStateModel.clear()
                            mStateModel.addAll(stateList)

                            mStatesNameList.clear()
                            for (faculty in mStateModel) {
                                mStatesNameList.add(faculty.name)
                            }

                            if (mStatesNameList.isNotEmpty()) {
                                setSpinnerState()
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                            // Handle JSON parsing exception
                        }
                    } else {
                        mStatesNameList.clear()
                        mStateId = -1;
                        setSpinnerState()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@BestCollegeActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiFaculties() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@BestCollegeActivity).myApi.api_Faculties()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val parsedData = UtilsFunctions().parseFacultyModel2Json(responseJson)
                        if (parsedData != null) {
                            mFacilitiesList.clear()
                            mFacilitiesList.addAll(parsedData)

                            mFacilitiesNameList.clear()
                            for (faculty in mFacilitiesList) {
                                mFacilitiesNameList.add(faculty.name)
                            }

                            if (mFacilitiesList.isNotEmpty()) {
                                setSpinnerFaculty()
                            }
                        } else {
                            resetSpinners()
                        }
                    } else {
                        resetSpinners()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@BestCollegeActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetSpinners() {

        //Clear Faculty Spinner
        mFacilitiesNameList.clear()
        mFacultyId = -1;
        setSpinnerFaculty()

        mStreamList.clear()
        binding.rvBestCollegeCategory.visibility = View.GONE
        mBestCollegeCategoryAdapter?.notifyDataSetChanged()


        binding.tvResults.visibility = View.GONE
        binding.rvBestCollege.visibility = View.GONE
        binding.llNoData.visibility = View.VISIBLE

    }


    private fun apiBestCollegeList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@BestCollegeActivity).myApi.api_BestCollegeList(
                mStateId,
                mFacultyId,
                mStreamId
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
                            binding.rvBestCollege.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE


                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvBestCollege.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.tvResults.visibility = View.GONE
                    binding.rvBestCollege.visibility = View.GONE
                    binding.llNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@BestCollegeActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun itemClickCallback(streamId: Int, position: Int) {
        mStreamId = streamId
        apiBestCollegeList()
    }


    override fun onResume() {
        super.onResume()
        apiBestCollegeList()
    }
}
