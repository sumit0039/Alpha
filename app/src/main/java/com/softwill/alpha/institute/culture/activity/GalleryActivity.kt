package com.softwill.alpha.institute.culture.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.institute.culture.adapter.GalleryAdapter
import com.softwill.alpha.institute.culture.model.PhotoModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryActivity : AppCompatActivity(), GalleryAdapter.CallbackInterface {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityGalleryBinding
    private var mDelayHandler: Handler? = null
    var mGalleryAdapter: GalleryAdapter? = null

    val mPhotoModel = java.util.ArrayList<PhotoModel>()

    var year = "2023"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_gallery
        )

        setupBack()
        setSpinnerYear()
        setupSwipeListener()
        setAdapter()
        apiCulturePictures()
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
                year = adapterView.getItemAtPosition(pos).toString()
                apiCulturePictures()

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun setAdapter() {
        mGalleryAdapter = GalleryAdapter(applicationContext, this, mPhotoModel)
        val layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position % 3) {
                    0, 1 -> {
                        1
                    }
                    2 -> {
                        2
                    }
                    else -> {
                        1
                    }
                }
            }
        }
        binding.rvGallery.setHasFixedSize(true)
        binding.rvGallery.setLayoutManager(layoutManager)
        binding.rvGallery.setItemAnimator(DefaultItemAnimator())
        binding.rvGallery.setAdapter(mGalleryAdapter)
        mGalleryAdapter!!.notifyDataSetChanged()
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
        apiCulturePictures()
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_gallery)
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

    override fun onImageCallback(position: Int, picUrl: String) {
        openImage(picUrl)
    }

    fun openImage(picUrl: String) {

        var inflater = LayoutInflater.from(this)
        var popupview = inflater.inflate(R.layout.popup_image, null, false)

        var image = popupview.findViewById<ImageView>(R.id.image)
        var ivClose = popupview.findViewById<ImageView>(R.id.ivClose)


        Glide.with(this@GalleryActivity).load(picUrl).placeholder(R.drawable.icon_no_image)
            .into(image)


        var builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.CENTER, 0, 0)

        image.setOnClickListener {
            builder.dismiss()
        }

    }


    private fun apiCulturePictures() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@GalleryActivity).myApi.api_CulturePictures(year)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<PhotoModel>>() {}.type
                        val mList: List<PhotoModel> =
                            Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mPhotoModel.clear()
                        mPhotoModel.addAll(mList)


                        if (mPhotoModel.isNotEmpty()) {
                            mGalleryAdapter?.notifyDataSetChanged()
                            binding.rvGallery.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvGallery.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvGallery.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@GalleryActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}