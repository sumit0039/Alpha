package com.softwill.alpha.institute.library.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityReadBookBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadBookBinding

    private var mScreenTitle: String? = null
    private var mThumbnailUrl: String? = null
    private var mPdfUrl: String? = null
    private var mName: String? = null
    private var mWriterName: String? = null
    private var mBookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_read_book)

        val bundle: Bundle? = intent.extras
        mScreenTitle = bundle?.getString("mScreenTitle")
        mBookId = bundle!!.getInt("mBookId")
        mThumbnailUrl = bundle.getString("mThumbnailUrl")
        mPdfUrl = bundle.getString("mPdfUrl")
        mName = bundle.getString("mName")
        mWriterName = bundle.getString("mWriterName")


        setupBack()
        Glide.with(this).load(mThumbnailUrl).placeholder(R.drawable.icon_no_image)
            .into(binding.ivImage)
        binding.tvName.text = mName
        binding.tvWriter.text = mWriterName

        binding.btnRead.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(mPdfUrl), "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                if (mBookId != -1){
                    apiMarkRecentRead()
                }

                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // PDF viewer app not found on the device
                Toast.makeText(this, "No PDF viewer app installed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun apiMarkRecentRead() {


        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReadBookActivity).myApi.api_MarkRecentRead(
                mBookId
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Save successfully") {

                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ReadBookActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ReadBookActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mScreenTitle
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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