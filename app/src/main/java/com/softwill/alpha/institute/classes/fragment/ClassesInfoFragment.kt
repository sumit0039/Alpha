package com.softwill.alpha.institute.classes.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentClassesInfoBinding
import com.softwill.alpha.institute.classes.activity.StudentDetailsActivity
import com.softwill.alpha.institute.classes.model.StudentGrades
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ClassesInfoFragment : Fragment() {

    private lateinit var binding: FragmentClassesInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_classes_info, container, false);

        apiStudentClassPerformance()

        return binding.root

    }


    private fun apiStudentClassPerformance() {
        val activity: StudentDetailsActivity? = activity as StudentDetailsActivity?

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireContext()).myApi.api_StudentClassPerformance(activity?.getStudentId()!!)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val studentGrades = Gson().fromJson(responseBody, StudentGrades::class.java)

                         binding.tvPerformance.text = studentGrades.performance.formatDecimal(2)
                         binding.tvExam.text = studentGrades.exam.formatDecimal(2)
                         binding.tvAssignment.text = studentGrades.assignment.formatDecimal(2)
                         binding.tvAttendance.text = studentGrades.attendance.formatDecimal(2)


                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, requireContext())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun Double.formatDecimal(digits: Int): String {
        return "%.${digits}f%%".format(this)
    }


}

