package com.softwill.alpha.career.career_guidance.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.adapter.CareerGuidanceMetaAdapter
import com.softwill.alpha.career.career_guidance.adapter.CareerPhotosAdapter
import com.softwill.alpha.career.career_guidance.adapter.CareerVideosAdapter
import com.softwill.alpha.career.career_guidance.model.CareerGuidanceDetailModel
import com.softwill.alpha.career.career_guidance.model.MetaDataModel
import com.softwill.alpha.career.career_guidance.model.VideoModel
import com.softwill.alpha.databinding.ActivityCareerGuidanceDetailBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CareerGuidanceDetailActivity : AppCompatActivity(),
    CareerVideosAdapter.CareerVideosAdapterCallbackInterface,
    CareerGuidanceMetaAdapter.CareerGuidanceMetaAdapterCallbackInterface {

    private lateinit var binding: ActivityCareerGuidanceDetailBinding
    var mName: String = ""
    var mId: Int = -1
    private lateinit var mCareerPhotosAdapter: CareerPhotosAdapter
    private lateinit var mCareerVideosAdapter: CareerVideosAdapter
    private lateinit var mCareerGuidanceMetaAdapter: CareerGuidanceMetaAdapter
    val mPhotoModel = ArrayList<PhotoModel>()
    val mVideoModel = ArrayList<VideoModel>()
    var mMetaDataModel = ArrayList<MetaDataModel>()
    var isPhotoOpened: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_career_guidance_detail)
        val bundle: Bundle? = intent.extras
        mName = bundle!!.getString("mName").toString()
        mId = bundle.getInt("mId")

        setupBack()
        setupClickListener()


        mCareerPhotosAdapter = CareerPhotosAdapter(this, mPhotoModel)
        binding.rvPhotos.adapter = mCareerPhotosAdapter
        mCareerPhotosAdapter.notifyDataSetChanged()

        mCareerVideosAdapter = CareerVideosAdapter(this, mVideoModel, this)
        binding.rvVideos.adapter = mCareerVideosAdapter
        mCareerVideosAdapter.notifyDataSetChanged()


        mCareerGuidanceMetaAdapter = CareerGuidanceMetaAdapter(mMetaDataModel, this, this)
        binding.rvCareerGuidanceMeta.adapter = mCareerGuidanceMetaAdapter
        mCareerGuidanceMetaAdapter.notifyDataSetChanged()

        apiCareerGuidanceDetail()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupClickListener() {


        binding.rlPhotos.setOnClickListener() {
            if (!isPhotoOpened) {
                isPhotoOpened = true
                binding.rvPhotos.visibility = View.VISIBLE
                binding.ivCard5.setImageDrawable(resources.getDrawable(R.drawable.icon_minus))
            } else {
                isPhotoOpened = false
                binding.rvPhotos.visibility = View.GONE
                binding.ivCard5.setImageDrawable(resources.getDrawable(R.drawable.icon_plus))
            }
        }

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mName
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

    private fun apiCareerGuidanceDetail() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@CareerGuidanceDetailActivity).myApi.api_CareerGuidanceDetail(
                mId
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        try {
                            val jsonArray = JSONArray(responseJson)
                            if (jsonArray.length() > 0) {
                                val jsonObject = jsonArray.getJSONObject(0)

                                val id = jsonObject.getInt("id")
                                val facultyName = jsonObject.getString("facultyName")
                                val streamName = jsonObject.getString("streamName")

                                val metaDataArray = jsonObject.getJSONArray("metaData")
                                for (i in 0 until metaDataArray.length()) {
                                    val metaDataObject = metaDataArray.getJSONObject(i)
                                    val title = metaDataObject.getString("title")
                                    val desc = metaDataObject.getString("desc")
                                    if (i == 0) {
                                        val metaData = MetaDataModel(0, title, desc, true)
                                        mMetaDataModel.add(metaData)
                                    } else {
                                        val metaData = MetaDataModel(0, title, desc, false)
                                        mMetaDataModel.add(metaData)
                                    }


                                }

                                mCareerGuidanceMetaAdapter.notifyDataSetChanged()

                                val photosArray = jsonObject.getJSONArray("photos")
                                for (i in 0 until photosArray.length()) {
                                    val photoObject = photosArray.getJSONObject(i)
                                    val pathUrl = photoObject.getString("pathUrl")
                                    val photo = PhotoModel(pathUrl)
                                    mPhotoModel.add(photo)
                                }

                                mCareerPhotosAdapter.notifyDataSetChanged()


                                val videosArray = jsonObject.getJSONArray("videos")
                                for (i in 0 until videosArray.length()) {
                                    val videoObject = videosArray.getJSONObject(i)
                                    val pathUrl = videoObject.getString("pathUrl")
                                    val video = VideoModel(pathUrl)
                                    mVideoModel.add(video)
                                }

                                mCareerVideosAdapter.notifyDataSetChanged()

                                val careerGuidanceDetailModel = CareerGuidanceDetailModel(
                                    id,
                                    facultyName,
                                    streamName,
                                    mMetaDataModel,
                                    mPhotoModel,
                                    mVideoModel
                                )


                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        // Handle empty response
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(
                        response,
                        this@CareerGuidanceDetailActivity
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun videoClickCallback(uri: String, position: Int) {
        playVideoPlay(uri)
    }

    fun playVideoPlay(uri: String) {

        val inflater = LayoutInflater.from(this)
        val popupview = inflater.inflate(R.layout.popup_video_player, null, false)

        val videoView = popupview.findViewById<VideoView>(R.id.videoView)
        val ibBack = popupview.findViewById<ImageButton>(R.id.ibBack)


        val builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.CENTER, 0, 0)


        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        val uri: Uri = Uri.parse(uri)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()



        ibBack.setOnClickListener {
            builder.dismiss()
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun titleClickCallback(position: Int) {
        for (i in 0 until mMetaDataModel.size) {
            mMetaDataModel[i].isOpen = i == position
        }
        mCareerGuidanceMetaAdapter.notifyDataSetChanged()
    }


}