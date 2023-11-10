package com.softwill.alpha.career.entrance_exam.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.MetaDataModel
import com.softwill.alpha.career.entrance_exam.adapter.EntranceExamMetaAdapter
import com.softwill.alpha.career.entrance_exam.model.EntranceExamModel
import com.softwill.alpha.databinding.ActivityEntranceExamDetailBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EntranceExamDetailActivity : AppCompatActivity(),
    EntranceExamMetaAdapter.EntranceExamMetaCallbackInterface {

    private lateinit var binding: ActivityEntranceExamDetailBinding

    private lateinit var mEntranceExamMetaAdapter: EntranceExamMetaAdapter
    var mMetaDataModel = ArrayList<MetaDataModel>()

    private var mExamName: String? = null
    private var mExamId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_entrance_exam_detail)

        val bundle: Bundle? = intent.extras
        mExamName = bundle?.getString("mExamName")
        mExamId = bundle!!.getInt("mExamId")

        setupBack()


        mEntranceExamMetaAdapter = EntranceExamMetaAdapter(mMetaDataModel, this, this)
        binding.rvEntranceExamMeta.adapter = mEntranceExamMetaAdapter
        mEntranceExamMetaAdapter.notifyDataSetChanged()


        apiEntranceExamDetail()
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mExamName.toString()
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);

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

    override fun titleClickCallback(position: Int) {
        for (i in 0 until mMetaDataModel.size) {
            mMetaDataModel[i].isOpen = i == position
        }
        mEntranceExamMetaAdapter.notifyDataSetChanged()
    }

    private fun apiEntranceExamDetail() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@EntranceExamDetailActivity).myApi.api_EntranceExamDetail(
                mExamId
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {

                        val examInfo = Gson().fromJson(responseJson, EntranceExamModel::class.java)
                        mMetaDataModel.clear()
                        var isFirstItem = true
                        val metaDataList = examInfo.metaData
                        for (metaData in metaDataList) {
                            val metaDataModel = MetaDataModel(
                                metaData.id,
                                metaData.title,
                                metaData.desc,
                                isFirstItem
                            )
                            mMetaDataModel.add(metaDataModel)
                            isFirstItem = false
                        }


                        mEntranceExamMetaAdapter.notifyDataSetChanged()
                    } else {
                        // Handle empty response
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(
                        response,
                        this@EntranceExamDetailActivity
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}