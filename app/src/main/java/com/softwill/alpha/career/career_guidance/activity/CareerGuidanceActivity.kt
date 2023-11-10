package com.softwill.alpha.career.career_guidance.activity

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
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.adapter.CareerGuidanceAdapter
import com.softwill.alpha.career.career_guidance.model.CareerGuidanceModel
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.databinding.ActivityCareerGuidanceBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CareerGuidanceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCareerGuidanceBinding
    var mCareerGuidanceAdapter: CareerGuidanceAdapter? = null

    val mFacilitiesList = ArrayList<FacultyModel2>()
    val mCareerGuidanceList = ArrayList<CareerGuidanceModel>()

    private var mFacultyId: Int = -1
    private var mStreamId: Int = -1
    val mFacilitiesNameList: ArrayList<String> = ArrayList()
    val mStreamsNameList: ArrayList<String> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_career_guidance)
        setupBack()

        apiFaculties()


        // binding.btnView.setOnClickListener(this)

        /* val data = ArrayList<ItemModel>()
         data.add(ItemModel("Architectural engineering"))
         data.add(ItemModel("biomedical engineering"))
         data.add(ItemModel("civil engineering"))
         data.add(ItemModel("mechanical engineering"))
         data.add(ItemModel("electrical engineering"))
         data.add(ItemModel("aerospace engineering"))
         data.add(ItemModel("computer science engineering"))
         data.add(ItemModel("chemical engineering"))
         data.add(ItemModel("nuclear engineering"))
         data.add(ItemModel("petroleum engineering"))*/


        mCareerGuidanceAdapter = CareerGuidanceAdapter(mCareerGuidanceList, this)
        binding.rvCareerGuidance.adapter = mCareerGuidanceAdapter
        mCareerGuidanceAdapter?.notifyDataSetChanged()

    }




    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_career_guidance)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


    }

    private fun setSpinnerFaculty() {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, mFacilitiesNameList)

        /* val monthSpinnerAdapter = ArrayAdapter.createFromResource(
             this,
             R.array.FacultyType,
             R.layout.simple_spinner_item
         )*/
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerFaculty.adapter = adapter
        binding.spinnerFaculty.setSelection(0)
        binding.spinnerFaculty.onItemSelectedListener = object :
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

                mFacultyId = mFacilitiesList[pos].facultyId


                mStreamsNameList.clear()
                for (streams in mFacilitiesList[pos].streams)

                    mStreamsNameList.add(streams.streamName)
                if (mStreamsNameList.isNotEmpty()) {
                    setSpinnerStream(pos)
                }

                System.out.println("mFacultyId : $mFacultyId")

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun setSpinnerStream(facultyPosition: Int) {
        /*val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.StreamType,
            R.layout.simple_spinner_item
        )*/
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, mStreamsNameList)

        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerStream.adapter = adapter
        binding.spinnerStream.setSelection(0)
        binding.spinnerStream.onItemSelectedListener = object :
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


                if (facultyPosition != -1) {
                    mStreamId = mFacilitiesList[facultyPosition].streams[pos].streamId


                    apiCareerGuidanceList()

                    System.out.println("mStreamId : $mStreamId")
                }


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
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
                val intent = Intent(applicationContext, SearchCareerGuidanceActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /* R.id.btnView -> {
                 binding.tvResults.visibility = View.VISIBLE
                 binding.rvCareerGuidance.visibility = View.VISIBLE
                 binding.llNoData.visibility = View.GONE
             }*/
        }
    }


    private fun apiFaculties() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CareerGuidanceActivity).myApi.api_Faculties()

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
                    UtilsFunctions().handleErrorResponse(response, this@CareerGuidanceActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiCareerGuidanceList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CareerGuidanceActivity).myApi.api_CareerGuidanceList(
                mFacultyId,
                mStreamId
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
                            binding.rvCareerGuidance.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mCareerGuidanceAdapter?.notifyDataSetChanged()

                        }else{
                            binding.tvResults.visibility = View.GONE
                            binding.rvCareerGuidance.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }

                    } else {
                        binding.tvResults.visibility = View.GONE
                        binding.rvCareerGuidance.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@CareerGuidanceActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun resetSpinners() {


        //Clear Faculty Spinner
        mFacilitiesNameList.clear()
        mFacultyId = -1;
        setSpinnerFaculty()


        //Clear Stream Spinner
        mStreamsNameList.clear()
        mStreamId = -1;
        setSpinnerStream(-1)


        binding.tvResults.visibility = View.GONE
        binding.rvCareerGuidance.visibility = View.GONE
        binding.llNoData.visibility = View.VISIBLE
    }

}