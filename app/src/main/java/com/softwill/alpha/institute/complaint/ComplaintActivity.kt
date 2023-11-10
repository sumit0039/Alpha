package com.softwill.alpha.institute.complaint

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityComplaintBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplaintActivity : AppCompatActivity() {


    private lateinit var binding: ActivityComplaintBinding
    private var mDelayHandler: Handler? = null
    var mComplaintAdapter: ComplaintAdapter? = null
    var mComplaintModel: ArrayList<ComplaintModel> = ArrayList()

    var year : String = "2023"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complaint)


        setupBack()
        setupSwipeListener()
        setSpinnerYear()
        setupAdapter()
        setupOnClickListener()
        apiComplainList()
    }

    private fun setupOnClickListener() {


        binding.fab.setOnClickListener {
            addComplaintBottomSheet()
        }


    }

    private fun addComplaintBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_complaint, null)


        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)



        btnSend.setOnClickListener {
                if (etTitle.text.toString().trim().isEmpty()) {
                    UtilsFunctions().showToast(this@ComplaintActivity, "Enter title")
                } else if (etDescription.text.toString().trim().isEmpty()) {
                    UtilsFunctions().showToast(this@ComplaintActivity, "Enter description")
                } else {
                    dialog.dismiss()
                    apiComplainCreate(etTitle.text.toString(), etDescription.text.toString())
                }

        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {

        mComplaintAdapter = ComplaintAdapter(applicationContext, mComplaintModel)
        binding.rvComplaint.adapter = mComplaintAdapter
        mComplaintAdapter!!.notifyDataSetChanged()

    }


    private fun setSpinnerYear() {
        val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.YearType,
            R.layout.simple_spinner_item
        )
        monthSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerYear.adapter = monthSpinnerAdapter
        binding.spinnerYear.setSelection(0)
        binding.spinnerYear.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                /* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                year  = adapterView.getItemAtPosition(pos).toString()
                apiComplainList()

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiComplainList()

    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = resources.getString(R.string.title_complaints)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true)


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


    private fun apiComplainList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ComplaintActivity).myApi.api_complainList(year)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<ComplaintModel>>() {}.type
                        val mList: List<ComplaintModel> = Gson().fromJson(responseBody, listType)

                        // Update the mComplaintModel list with the new data
                        mComplaintModel.clear()
                        mComplaintModel.addAll(mList)


                        if (mComplaintModel.isNotEmpty()) {
                            mComplaintAdapter?.notifyDataSetChanged()
                            binding.rvComplaint.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvComplaint.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvComplaint.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@ComplaintActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiComplainCreate(etTitle: String, etDescription: String) {

        val jsonObject = JsonObject().apply {
            addProperty("title", etTitle)
            addProperty("desc", etDescription)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ComplaintActivity).myApi.api_complainCreate(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Added successfully") {

                            apiComplainList()

                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ComplaintActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ComplaintActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}