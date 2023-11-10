package com.softwill.alpha.profile_guest.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.dashboard.DashboardActivity
import com.softwill.alpha.databinding.ActivityProfileGuestBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.adapter.ProfileGuestTabAdapter
import com.softwill.alpha.profile_guest.model.GuestUserDetailsResponse
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileGuestActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityProfileGuestBinding
    private var mUserId: Int = -1
    private var mName: String = ""
    private var mProfileImage: String = ""
    private var isFriend: Boolean = false
    lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_guest)


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUserId = bundle.getInt("mUserId")

        }
        setupBack()

        binding.llConnections.setOnClickListener(this)

        setOnClickListener()
        setupViewPager()

        apiGuestUserDetails();
    }

    private fun setupViewPager() {
        val adapter = ProfileGuestTabAdapter(this, supportFragmentManager, 2, mUserId)
        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun setOnClickListener() {

        binding.cardConnect.setOnClickListener {
            apiAcceptRejectConnectionRequest()

        }


        binding.card1.setOnClickListener {
            apiRemoveConnection()
        }

        binding.card2.setOnClickListener {
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("mName", mName)
            startActivity(intent)
        }

        binding.ivProfileImage.setOnClickListener {
            openProfileImage()
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun openProfileImage() {

        val inflater = LayoutInflater.from(this)
        val popupview = inflater.inflate(R.layout.popup_profile_image, null, false)

        val image = popupview.findViewById<ImageView>(R.id.image)
        val ibBack = popupview.findViewById<ImageButton>(R.id.ibBack)

        Glide.with(this@ProfileGuestActivity).load(mProfileImage)
            .placeholder(R.drawable.icon_no_image).into(binding.ivProfileImage)

        val builder = PopupWindow(
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

        ibBack.setOnClickListener {
            builder.dismiss()
        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_profile)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.more_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
            com.softwill.alpha.R.id.menu_more -> {
                reportBlockBottomSheet()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun reportBlockBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_report_block, null)


        val btnReport = view.findViewById<Button>(R.id.btnReport)
        val btnBlock = view.findViewById<Button>(R.id.btnBlock)

        btnBlock.setOnClickListener {
            apiBlockUser()
            dialog.dismiss()
        }

        btnReport.setOnClickListener {
            dialog.dismiss()
            reportAbuseCallback()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    fun reportAbuseCallback() {

        val dialog = BottomSheetDialog(this@ProfileGuestActivity, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_report_abuse, null)

        val btnReport = view.findViewById<Button>(R.id.btnReport)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        var Clicked = false;
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Clicked = true
            radioButton = radioGroup.findViewById(checkedId)
        }

        btnReport.setOnClickListener {
            if (Clicked) {
                apiReportUser(radioButton.text.toString())
                dialog.dismiss()
            } else {
                UtilsFunctions().showToast(
                    this@ProfileGuestActivity,
                    "Please select any one to report"
                )
            }

        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }

    private fun apiReportUser(value: String) {
        val jsonObject = JsonObject().apply {
            addProperty("reportOn", "user")
            addProperty("itemId", mUserId)
            addProperty("report", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ProfileGuestActivity).myApi.api_ReportUser(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        UtilsFunctions().showToast(
                            this@ProfileGuestActivity, responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ProfileGuestActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ProfileGuestActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiBlockUser() {
        val jsonObject = JsonObject().apply {
            addProperty("userId", mUserId)
            addProperty("status", "block")
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ProfileGuestActivity).myApi.api_BlockUnblockUser(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Blocked successfully") {

                            val intent =
                                Intent(this@ProfileGuestActivity, DashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                            startActivity(intent)
                        }


                        UtilsFunctions().showToast(
                            this@ProfileGuestActivity,
                            responseObject.getString("message")
                        )


                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ProfileGuestActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ProfileGuestActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.llConnections -> {
                if (isFriend) {
                    val intent = Intent(applicationContext, ConnectionsActivity::class.java)
                    intent.putExtra("mUserId", mUserId)
                    startActivity(intent)
                }
            }

        }
    }

    private fun apiGuestUserDetails() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ProfileGuestActivity).myApi.api_GuestUserDetails(mUserId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject =
                        Gson().fromJson(responseJson, GuestUserDetailsResponse::class.java)


                    mName = responseObject.firstName + " " + responseObject.lastName
                    binding.tvName.text = mName
                    mProfileImage = responseObject.avtarUrl.toString()
                    Glide.with(this@ProfileGuestActivity).load(mProfileImage)
                        .placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)
                    if (responseObject.userName != null) {
                        binding.tvUserName.text = responseObject.userName
                    }
                    binding.tvBio.text = responseObject.bio


                    if (responseObject.friends) {
                        isFriend = true;
                        binding.ivBlueTick.isVisible = true
                        binding.linearLayout2.isVisible = false
                        binding.linearLayout.isVisible = true
                        binding.llLock.visibility = View.GONE
                        binding.viewPager.visibility = View.VISIBLE
                    } else {
                        isFriend = false
                        binding.ivBlueTick.isVisible = false
                        binding.linearLayout2.isVisible = true
                        binding.linearLayout.isVisible = false
                        binding.llLock.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.GONE
                    }

                    if (responseObject.connections > 1) {
                        binding.tvConnection.text = getString(R.string.title_Connections)
                    }

                    binding.tvConnectionCount.text = responseObject.connections.toString()


                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ProfileGuestActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiAcceptRejectConnectionRequest() {
        val jsonObject = JsonObject().apply {
            addProperty("userId", mUserId)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ProfileGuestActivity).myApi.api_SendConnectionRequest(
                jsonObject
            )


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Request sent successfully") {
                            binding.linearLayout2.isVisible = false
                            binding.linearLayout.isVisible = true
                            // binding.blur.setBlurRadius(0F)
                            binding.llLock.visibility = View.GONE
                            binding.viewPager.visibility = View.VISIBLE
                        }
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ProfileGuestActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun apiRemoveConnection() {
        val jsonObject = JsonObject().apply {
            addProperty("userId", mUserId)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ProfileGuestActivity).myApi.api_RemoveConnection(
                jsonObject
            )


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Cancelled successfully") {
                            binding.linearLayout2.isVisible = true
                            binding.linearLayout.isVisible = false
                            // binding.blur.setBlurRadius(10F)
                            binding.llLock.visibility = View.VISIBLE
                            binding.viewPager.visibility = View.GONE
                        }
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ProfileGuestActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}