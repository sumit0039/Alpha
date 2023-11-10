package com.softwill.alpha.profile.tabAbout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentAboutBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.model.GuestUserDetailsResponse
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.Constant.Companion.bloodGroup
import com.softwill.alpha.utils.Constant.Companion.dob
import com.softwill.alpha.utils.Constant.Companion.email
import com.softwill.alpha.utils.Constant.Companion.gender
import com.softwill.alpha.utils.Constant.Companion.hobbiesList
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    var mIsGuestUser: Boolean = false
    var mUserId: Int = -1
    var yourPreference: YourPreference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPreference = YourPreference(requireActivity())


        if (mIsGuestUser) {
            apiGuestUserDetails()
        } else {
            setProfile(
                yourPreference?.getData(gender),
                yourPreference?.getData(Constant.mobile),
                yourPreference?.getData(dob),
                yourPreference?.getData(bloodGroup),
                yourPreference?.getData(email),
                yourPreference?.getData(hobbiesList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(","),
                yourPreference?.getData(Constant.achievementsList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(","),
                yourPreference?.getData(Constant.skillsList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(",")
            )
        }

    }

    override fun onResume() {
        if (mIsGuestUser) {
            apiGuestUserDetails()
        }else{
            setProfile(
                yourPreference?.getData(gender),
                yourPreference?.getData(Constant.mobile),
                yourPreference?.getData(dob),
                yourPreference?.getData(bloodGroup),
                yourPreference?.getData(email),
                yourPreference?.getData(hobbiesList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(","),
                yourPreference?.getData(Constant.achievementsList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(","),
                yourPreference?.getData(Constant.skillsList)
                    ?.removeSurrounding("[", "]") // Remove the square brackets
                    ?.split(",")
            );
        }
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun setProfile(
        gender: String?,
        number: String?,
        dob: String?,
        bloodGroup: String?,
        email: String?,
        HobbilesElements: List<String>?,
        AchievementsElements: List<String>?,
        skillsElements: List<String>?,
    ) {


        binding.tvNumber.text = number

        if (dob != null && dob != "null") {
            binding.tvDateOfBirth.text = dob
        }

        if (gender != null && gender != "null") {
            binding.tvGender.text = UtilsFunctions().capitalize(gender)
        }

        if (bloodGroup != null && bloodGroup != "null") {
            binding.tvBloodGroup.text = bloodGroup
        }

        if (email != null && email != "null") {
            binding.tvEmail.text = email
        }


        if (!HobbilesElements.isNullOrEmpty() && HobbilesElements.any { it.isNotBlank() }) {
            val numberedHobbies = StringBuilder()
            for ((index, hobby) in HobbilesElements.withIndex()) {
                val number = index + 1
                val formattedHobby = hobby.replace(" ", "").replace("\"", "")
                numberedHobbies.append("$number. $formattedHobby")
                if (index < HobbilesElements.size - 1) {
                    numberedHobbies.append("\n")
                }
            }
            binding.tvHobbies.text = numberedHobbies.toString()
        }


        if (!AchievementsElements.isNullOrEmpty() && AchievementsElements.any { it.isNotBlank() }) {
            val numberedAchievements = StringBuilder()
            for ((index, achievement) in AchievementsElements.withIndex()) {
                val number = index + 1
                val formattedSkill = achievement.replace(" ", "").replace("\"", "")
                numberedAchievements.append("$number. $formattedSkill")
                if (index < AchievementsElements.size - 1) {
                    numberedAchievements.append("\n")
                }
            }
            binding.tvAchievements.text = numberedAchievements.toString()
        }


        if (!skillsElements.isNullOrEmpty() && skillsElements.any { it.isNotBlank() }) {
            val numberedSkills = StringBuilder()
            for ((index, skill) in skillsElements.withIndex()) {
                val number = index + 1
                val formattedSkill = skill.replace(" ", "").replace("\"", "")
                numberedSkills.append("$number. $formattedSkill")
                if (index < skillsElements.size - 1) {
                    numberedSkills.append("\n")
                }
            }
            binding.tvSkills.text = numberedSkills.toString()
        }

    }


    companion object {
        fun newInstance(mIsGuestUser: Boolean, mUserId: Int): AboutFragment {
            val fragment = AboutFragment()
            fragment.mIsGuestUser = mIsGuestUser
            fragment.mUserId = mUserId
            return fragment
        }
    }

    private fun apiGuestUserDetails() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_GuestUserDetails(mUserId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject =
                        Gson().fromJson(responseJson, GuestUserDetailsResponse::class.java)


                    setProfile(
                        responseObject.gender,
                        responseObject.mobile,
                        responseObject.dob,
                        responseObject.bloodGroup,
                        responseObject.email,
                        responseObject.hobbies.toString()
                            .removeSurrounding("[", "]") // Remove the square brackets
                            .split(","),
                        responseObject.achievements.toString()
                            .removeSurrounding("[", "]") // Remove the square brackets
                            .split(","),
                        responseObject.skills.toString()
                            .removeSurrounding("[", "]") // Remove the square brackets
                            .split(",")
                    )


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