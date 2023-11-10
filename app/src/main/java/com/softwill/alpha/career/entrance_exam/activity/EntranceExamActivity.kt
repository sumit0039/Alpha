package com.softwill.alpha.career.entrance_exam.activity

import android.annotation.SuppressLint
import android.content.Intent
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
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.career.entrance_exam.adapter.EntranceExamFacultiesAdapter
import com.softwill.alpha.career.entrance_exam.adapter.EntranceExamResultAdapter
import com.softwill.alpha.career.entrance_exam.model.EntranceExamModel
import com.softwill.alpha.databinding.ActivityEntranceExamBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EntranceExamActivity : AppCompatActivity(),
    EntranceExamFacultiesAdapter.EntranceFacultiesCallbackInterface {

    private lateinit var binding: ActivityEntranceExamBinding
    var mEntranceExamFacultiesAdapter: EntranceExamFacultiesAdapter? = null
    var mEntranceExamResultAdapter: EntranceExamResultAdapter? = null


    val mEntranceExamList = java.util.ArrayList<EntranceExamModel>()
    val mFacilitiesList = ArrayList<FacultyModel2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_entrance_exam)
        setupBack()

        // setupSwipeListener()
        setupEntranceExamFacultiesAdapter()
        setupEntranceExamResultAdapter()

        apiFaculties();
    }


    private fun setupEntranceExamFacultiesAdapter() {


        mEntranceExamFacultiesAdapter =
            EntranceExamFacultiesAdapter(mFacilitiesList, this@EntranceExamActivity, this)
        binding.rvEntranceFaculties.adapter = mEntranceExamFacultiesAdapter
        mEntranceExamFacultiesAdapter?.notifyDataSetChanged()
    }

    private fun setupEntranceExamResultAdapter() {

        mEntranceExamResultAdapter =
            EntranceExamResultAdapter(mEntranceExamList, this@EntranceExamActivity)
        binding.rvEntranceExamResult.adapter = mEntranceExamResultAdapter
        mEntranceExamResultAdapter?.notifyDataSetChanged()
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.entrance_exam)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.search_menu3, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_search -> {
                val intent = Intent(applicationContext, SearchEntranceExamActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun apiFaculties() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EntranceExamActivity).myApi.api_Faculties()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val parsedData = UtilsFunctions().parseFacultyModel2Json(responseJson)
                        if (parsedData != null) {
                            mFacilitiesList.clear()
                            mFacilitiesList.addAll(parsedData)

                            binding.rvEntranceFaculties.visibility = View.VISIBLE
                            mEntranceExamFacultiesAdapter?.notifyDataSetChanged()

                            apiEntranceExamList(mFacilitiesList[0].facultyId)
                        } else {
                            binding.rvEntranceFaculties.visibility = View.GONE
                        }
                    } else {
                        binding.rvEntranceFaculties.visibility = View.GONE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@EntranceExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun itemClickCallback(facultyId: Int, position: Int) {
        apiEntranceExamList(facultyId)
    }


    private fun apiEntranceExamList(facultyId: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EntranceExamActivity).myApi.api_EntranceExamList(
                facultyId
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
                            binding.rvEntranceExamResult.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mEntranceExamResultAdapter?.notifyDataSetChanged()
                        } else {
                            binding.tvResults.visibility = View.GONE
                            binding.rvEntranceExamResult.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.tvResults.visibility = View.GONE
                        binding.rvEntranceExamResult.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@EntranceExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}