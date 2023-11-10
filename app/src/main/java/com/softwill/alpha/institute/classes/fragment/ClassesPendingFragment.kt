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
import com.softwill.alpha.databinding.FragmentClassesPendingBinding
import com.softwill.alpha.institute.classes.activity.StudentDetailsActivity
import com.softwill.alpha.institute.classes.adapter.PendingAssignmentAdapter
import com.softwill.alpha.institute.classes.adapter.PendingExamAdapter
import com.softwill.alpha.institute.classes.model.PendingAssignment
import com.softwill.alpha.institute.classes.model.PendingExam
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClassesPendingFragment : Fragment() {

    private lateinit var binding: FragmentClassesPendingBinding

    private var mPendingAssignmentAdapter: PendingAssignmentAdapter? = null
    private val mPendingAssignment = java.util.ArrayList<PendingAssignment>()


    private var mPendingExamAdapter: PendingExamAdapter? = null
    private val mPendingExam = java.util.ArrayList<PendingExam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_classes_pending, container, false);

        setupStudentSubjectAdapter()

        apiStudentPendingAssignment()
        apiStudentPendingExam()


        return binding.root

    }

    private fun setupStudentSubjectAdapter() {
        mPendingAssignmentAdapter = PendingAssignmentAdapter(requireContext(), mPendingAssignment)
        binding.rvPendingAssignment.adapter = mPendingAssignmentAdapter
        mPendingAssignmentAdapter!!.notifyDataSetChanged()

        mPendingExamAdapter = PendingExamAdapter(requireContext(), mPendingExam)
        binding.rvPendingExam.adapter = mPendingExamAdapter
        mPendingExamAdapter!!.notifyDataSetChanged()

    }


    private fun apiStudentPendingAssignment() {
        val activity: StudentDetailsActivity? = activity as StudentDetailsActivity?

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireContext()).myApi.api_StudentPendingAssignment(activity?.getStudentId()!!)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<PendingAssignment>>() {}.type
                        val mList: List<PendingAssignment> = Gson().fromJson(responseBody, listType)

                        // Update the mPendingAssignment list with the new data
                        mPendingAssignment.clear()
                        mPendingAssignment.addAll(mList)


                        if (mPendingAssignment.isNotEmpty()) {
                            mPendingAssignmentAdapter?.notifyDataSetChanged()
                            binding.rvPendingAssignment.visibility = View.VISIBLE
                        } else {
                            binding.rvPendingAssignment.visibility = View.GONE
                        }


                    }
                } else {
                    binding.rvPendingAssignment.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, requireContext())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiStudentPendingExam() {
        val activity: StudentDetailsActivity? = activity as StudentDetailsActivity?

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireContext()).myApi.api_StudentPendingExam(activity?.getStudentId()!!)


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<PendingExam>>() {}.type
                        val mList: List<PendingExam> = Gson().fromJson(responseBody, listType)

                        // Update the mPendingAssignment list with the new data
                        mPendingExam.clear()
                        mPendingExam.addAll(mList)


                        if (mPendingExam.isNotEmpty()) {
                            mPendingExamAdapter?.notifyDataSetChanged()
                            binding.rvPendingExam.visibility = View.VISIBLE
                        } else {
                            binding.rvPendingExam.visibility = View.GONE
                        }


                    }
                } else {
                    binding.rvPendingExam.visibility = View.GONE
                    UtilsFunctions().handleErrorResponse(response, requireContext())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}