package com.softwill.alpha.institute.classes.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentClassesSubjectBinding
import com.softwill.alpha.institute.classes.activity.StudentDetailsActivity
import com.softwill.alpha.institute.classes.adapter.StudentSubjectAdapter
import com.softwill.alpha.institute.classes.model.ClassSubject
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClassesSubjectFragment : Fragment() {

    private lateinit var binding: FragmentClassesSubjectBinding
    private var mStudentSubjectAdapter: StudentSubjectAdapter? = null

    val mClassSubject = java.util.ArrayList<ClassSubject>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_classes_subject, container, false);

        setupStudentSubjectAdapter()

        apiStudentClassSubjects()

        return binding.root

    }

    private fun setupStudentSubjectAdapter() {
        mStudentSubjectAdapter = StudentSubjectAdapter(requireContext(), mClassSubject)
        binding.rvStudentSubject.adapter = mStudentSubjectAdapter
        mStudentSubjectAdapter!!.notifyDataSetChanged()
    }


    private fun apiStudentClassSubjects() {
        val activity: StudentDetailsActivity? = activity as StudentDetailsActivity?

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireContext()).myApi.api_StudentClassSubjects(activity?.getStudentId()!!)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<ClassSubject>>() {}.type
                        val mList: List<ClassSubject> = Gson().fromJson(responseBody, listType)

                        // Update the mLectureTeacher list with the new data
                        mClassSubject.clear()
                        mClassSubject.addAll(mList)


                        if (mClassSubject.isNotEmpty()) {
                            mStudentSubjectAdapter?.notifyDataSetChanged()
                            binding.rvStudentSubject.visibility = View.VISIBLE
                        } else {
                            binding.rvStudentSubject.visibility = View.GONE
                        }


                    }
                } else {
                    binding.rvStudentSubject.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, requireContext())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}