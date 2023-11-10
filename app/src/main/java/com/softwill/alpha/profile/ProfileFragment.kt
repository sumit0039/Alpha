package com.softwill.alpha.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.softwill.alpha.R
import com.softwill.alpha.R.layout
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.activity.ActivityActivity
import com.softwill.alpha.profile.editProfile.EditProfileActivity
import com.softwill.alpha.profile.post.PostActivity
import com.softwill.alpha.profile.privacy.PrivacyActivity
import com.softwill.alpha.profile.rate.RateActivity
import com.softwill.alpha.profile.setting.SettingActivity
import com.softwill.alpha.profile_guest.activity.ConnectionsActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: com.softwill.alpha.databinding.FragmentProfileBinding
    var yourPreference: YourPreference? = null
    var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layout.fragment_profile, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yourPreference = YourPreference(requireActivity())

        apiCurrentUserDetails()
        setProfile()



        onClickListener()

        binding.llConnections.setOnClickListener(this)


        val adapter = ProfileTabAdapter(this, childFragmentManager, 2)
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

        binding.card1.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.card2.setOnClickListener {
            val intent = Intent(activity, PostActivity::class.java)
            startActivity(intent)
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setProfile() {
        var firstName = yourPreference?.getData(Constant.firstName)
        var lastName = yourPreference?.getData(Constant.lastName)
        var userName = yourPreference?.getData(Constant.userName)
        var bio = yourPreference?.getData(Constant.bio)
        var avtarUrl = yourPreference?.getData(Constant.avtarUrl)
        var connectionCount = yourPreference?.getData(Constant.connections)


        binding.tvName.text = "$firstName $lastName"
        if (userName != null && userName != "null") {
            binding.tvUserName.text = "${yourPreference?.getData(Constant.userName)}"
        }

        if (bio != null && bio != "null") {
            binding.tvBio.text = "${yourPreference?.getData(Constant.bio)}"
        }

        if (connectionCount != null) {
            if (connectionCount.toInt() > 1) {
                if (isAdded) {
                    binding.tvConnection.text = getString(R.string.title_Connections)
                }
            }
        }

        binding.tvConnectionCount.text = connectionCount.toString()




        if (!avtarUrl.isNullOrEmpty() && avtarUrl != "null") {
            if (isAdded) {
                Glide.with(requireActivity()).load(avtarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)
            }

        }

    }

    private fun onClickListener() {


        binding.ivMore.setOnClickListener {
            val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
            val view = layoutInflater.inflate(R.layout.bottomsheet_profile_more, null)

            val llPrivacy = view.findViewById<LinearLayout>(R.id.llPrivacy)
            val llWebScan = view.findViewById<LinearLayout>(R.id.llWebScan)
            val llSetting = view.findViewById<LinearLayout>(R.id.llSetting)
            val llRate = view.findViewById<LinearLayout>(R.id.llRate)
            val llActivity = view.findViewById<LinearLayout>(R.id.llActivity)

            llActivity.setOnClickListener {
                dialog?.dismiss()
                val intent = Intent(activity, ActivityActivity::class.java)
                activity?.startActivity(intent)
            }

            llPrivacy.setOnClickListener {
                dialog?.dismiss()
                val intent = Intent(activity, PrivacyActivity::class.java)
                activity?.startActivity(intent)
            }

            llSetting.setOnClickListener {
                dialog?.dismiss()
                val intent = Intent(activity, SettingActivity::class.java)
                activity?.startActivity(intent)
            }

            llWebScan.setOnClickListener {
                dialog?.dismiss()
            }


            llRate.setOnClickListener {
                dialog?.dismiss()
                val intent = Intent(activity, RateActivity::class.java)
                activity?.startActivity(intent)
            }


            dialog?.setCanceledOnTouchOutside(true)
            dialog?.setCancelable(true)
            dialog?.setContentView(view)
            dialog?.show()


            /*val popupMenu = PopupMenu(activity, binding.ivSignOut)

            popupMenu.menuInflater.inflate(menu.profile_menu, popupMenu.getMenu())
            popupMenu.setOnMenuItemClickListener {
                val intent = Intent(activity, SignInActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
                true
            }
            popupMenu.show()*/
        }


        binding.ivProfileImage.setOnClickListener {
            // openPostImage()
        }
    }

    private fun openPostImage() {

        var inflater = LayoutInflater.from(context)
        var popupview = inflater.inflate(R.layout.popup_post, null, false)


        var builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.CENTER, 0, 0)

        popupview.setOnClickListener {
            builder.dismiss()
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.llConnections -> {
                val intent = Intent(context, ConnectionsActivity::class.java)
                startActivity(intent)
            }

        }
    }

    override fun onResume() {
        apiCurrentUserDetails()
        super.onResume()
    }


    private fun apiCurrentUserDetails() {
       // progressDialog = UtilsFunctions().showCustomProgressDialog(requireContext())

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_CurrentUserDetails()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)


                    // Parse the JSON array from the responseObject
                    val hobbiesArray: JSONArray? = responseObject.optJSONArray("hobbies")
                    val skillsArray: JSONArray? = responseObject.optJSONArray("skills")
                    val achievementsArray: JSONArray? = responseObject.optJSONArray("achievements")


                    YourPreference.saveData(Constant.userId, responseObject.getInt("id"))
                    YourPreference.saveData(Constant.avtarUrl, responseObject.getString("avtarUrl"))
                    YourPreference.saveData(
                        Constant.firstName,
                        responseObject.getString("firstName")
                    )
                    YourPreference.saveData(Constant.lastName, responseObject.getString("lastName"))
                    YourPreference.saveData(Constant.mobile, responseObject.getString("mobile"))
                    YourPreference.saveData(Constant.userName, responseObject.getString("userName"))
                    YourPreference.saveData(Constant.bio, responseObject.getString("bio"))
                    YourPreference.saveData(Constant.email, responseObject.getString("email"))
                    YourPreference.saveData(Constant.dob, responseObject.getString("dob"))
                    YourPreference.saveData(Constant.gender, responseObject.getString("gender"))
                    YourPreference.saveData(
                        Constant.bloodGroup, responseObject.getString("bloodGroup")
                    )
                    YourPreference.saveData(
                        Constant.connections, responseObject.getInt("connections")
                    )
                    YourPreference.saveData(Constant.hobbiesList, hobbiesArray.toString())
                    YourPreference.saveData(Constant.skillsList, skillsArray.toString())
                    YourPreference.saveData(Constant.achievementsList, achievementsArray.toString())

                    if (responseObject.has("student")) {
                        val student = responseObject.getJSONObject("student")
                        YourPreference.saveData(Constant.instituteId, student.getInt("instituteId"))
                        YourPreference.saveData(
                            Constant.instituteFacultyId, student.getInt("instituteFacultyId")
                        )
                        YourPreference.saveData(
                            Constant.instituteStreamId, student.getInt("instituteStreamId")
                        )
                        YourPreference.saveData(
                            Constant.instituteStreamClassId,
                            student.getInt("instituteStreamClassId")
                        )
                        YourPreference.saveData(
                            Constant.instituteName, student.getString("instituteName")
                        )
                        YourPreference.saveData(
                            Constant.instituteUsername, student.getString("instituteUsername")
                        )
                        YourPreference.saveData(
                            Constant.facultyName, student.getString("facultyName")
                        )
                        YourPreference.saveData(Constant.className, student.getString("className"))
                        YourPreference.saveData(
                            Constant.streamName, student.getString("streamName")
                        )
                    } else if (responseObject.has("teacher")) {
                        val teacher = responseObject.getJSONObject("teacher")

                        YourPreference.saveData(Constant.instituteId, teacher.getInt("instituteId"))
                        YourPreference.saveData(
                            Constant.instituteFacultyId, teacher.getInt("instituteFacultyId")
                        )
                        YourPreference.saveData(
                            Constant.instituteName, teacher.getString("instituteName")
                        )
                        YourPreference.saveData(
                            Constant.instituteUsername, teacher.getString("instituteUsername")
                        )
                        YourPreference.saveData(
                            Constant.facultyName, teacher.getString("facultyName")
                        )
                    }


                    setProfile()

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}