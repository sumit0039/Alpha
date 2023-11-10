package com.softwill.alpha.institute.diary

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
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
import com.softwill.alpha.databinding.ActivityDiaryBinding
import com.softwill.alpha.institute.diary.DairyAdapter.DiaryAdapterCallbackInterface
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DiaryActivity : AppCompatActivity(), DiaryAdapterCallbackInterface {

    private lateinit var binding: ActivityDiaryBinding
    var mDairyAdapter: DairyAdapter? = null

    val mDairyModel = java.util.ArrayList<DairyModel>()

    var year = "2023"
    var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary)

        setupBack()
        setupAdapter()
        setupOnClickListener()


        setSpinnerYear()
        apiDairyList()
    }

    /*private fun getCurrentMonth() {
        val dateFormat: DateFormat = SimpleDateFormat("MM")
        val date = Date()
        currentMonth = dateFormat.format(date).toInt()

    }*/


    private fun setupOnClickListener() {

        binding.tvToday.setOnClickListener {
            apiDairyList()
        }


        binding.fab.setOnClickListener {

            addEditDiaryNoteBottomSheet("", "", false, -1)
        }


    }

    private fun addEditDiaryNoteBottomSheet(title: String, desc: String, edit: Boolean, id: Int) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_dairy_note, null)


        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)

        if (edit) tvTitle.text = resources.getString(R.string.edit_diary)
        if (edit) btnSave.text = resources.getString(R.string.update)
        etTitle.setText(title)
        etDescription.setText(desc)




        btnSave.setOnClickListener {

            var title = etTitle.text.toString().trim()
            var description = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter title")
            } else if (description.isEmpty()) {
                UtilsFunctions().showToast(this, "Enter desc")
            } else {
                dialog.dismiss()
                if (!edit) {
                    apiUpdateCreateDairy(title, description, -1)
                } else {
                    apiUpdateCreateDairy(title, description, id)
                }
            }


        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }


    private fun setSpinnerYear() {

        val monthSpinnerAdapter = ArrayAdapter.createFromResource(
            applicationContext, R.array.YearType, R.layout.simple_spinner_item
        )
        monthSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthSpinnerAdapter
        binding.spinnerMonth.setSelection(0)
        binding.spinnerMonth.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {/* Resources res = getResources();
                 String[] val = res.getStringArray(R.array.sortedBy);*/
                year = adapterView.getItemAtPosition(pos).toString()

                apiDairyList()


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun apiDairyList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@DiaryActivity).myApi.api_DairyList(year)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<DairyModel>>() {}.type
                        val mList: List<DairyModel> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mDairyModel.clear()
                        mDairyModel.addAll(mList)


                        if (mDairyModel.isNotEmpty()) {
                            mDairyAdapter?.notifyDataSetChanged()
                            binding.rvDiary.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvDiary.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvDiary.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@DiaryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupAdapter() {


        mDairyAdapter = DairyAdapter(mDairyModel, this@DiaryActivity, this)
        binding.rvDiary.adapter = mDairyAdapter
        mDairyAdapter!!.notifyDataSetChanged()


    }


    private fun apiUpdateCreateDairy(title: String, desc: String, id: Int) {

        progressDialog = UtilsFunctions().showCustomProgressDialog(this@DiaryActivity)

        val jsonObject = JsonObject().apply {
            addProperty("title", title)
            addProperty("desc", desc)
        }

        val retrofit = RetrofitClient.getInstance(this@DiaryActivity).myApi
        val call: Call<ResponseBody> = when (id) {
            -1 -> retrofit.api_CreateDairy(jsonObject)
            else -> retrofit.api_UpdateDairy(id, jsonObject)
        }


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Added successfully" || message == "Updated successfully") {
                            apiDairyList()
                        }

                    } else if (responseObject.has("error")) {
                        progressDialog?.dismiss()
                        UtilsFunctions().showToast(
                            this@DiaryActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@DiaryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_diary)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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


    override fun detailsCallback(position: Int, id: Int, title: String, desc: String) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_dairy_details, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)


        tvTitle.text = title
        tvDesc.text = desc

        btnDelete.setOnClickListener {
            dialog.dismiss()
            apiDeleteDairy(id, position)


        }

        btnEdit.setOnClickListener {
            dialog.dismiss()
            addEditDiaryNoteBottomSheet(title, desc, true, id)
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        apiDairyList()
    }


    private fun apiDeleteDairy(id: Int, position: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@DiaryActivity).myApi.api_DeleteDairy(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Deleted successfully") {
                            mDairyAdapter?.removeItem(position)
                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@DiaryActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@DiaryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}