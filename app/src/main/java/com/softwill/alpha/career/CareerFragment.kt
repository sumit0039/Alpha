package com.softwill.alpha.career

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.softwill.alpha.R
import com.softwill.alpha.career.best_college.activity.BestCollegeActivity
import com.softwill.alpha.career.career_guidance.activity.CareerGuidanceActivity
import com.softwill.alpha.career.education_loan.EducationLoanActivity
import com.softwill.alpha.career.entrance_exam.activity.EntranceExamActivity
import com.softwill.alpha.career.mack_exam.activity.MackExamActivity
import com.softwill.alpha.databinding.FragmentCareerBinding


class CareerFragment : Fragment() {

    private lateinit var binding: FragmentCareerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_career, container, false);
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListener()
    }

    private fun onClickListener() {

        binding.llCareer.setOnClickListener {
            val intent = Intent(activity, CareerGuidanceActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llBestCollege.setOnClickListener {
            val intent = Intent(activity, BestCollegeActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llLoans.setOnClickListener {
            val intent = Intent(activity, EducationLoanActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llMackExam.setOnClickListener {
            val intent = Intent(activity, MackExamActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llEntranceExam.setOnClickListener {
            val intent = Intent(activity, EntranceExamActivity::class.java)
            activity?.startActivity(intent)
        }

    }
}