package com.softwill.alpha.institute.online_exam.teacher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeacherExamCompletedBinding
import com.softwill.alpha.institute.online_exam.teacher.model.TeacherCompletedExam
import com.softwill.alpha.utils.UtilsFunctions


class TeacherExamCompletedAdapter(
    private val context: Context,
    private var mList: ArrayList<TeacherCompletedExam>,
) :
    RecyclerView.Adapter<TeacherExamCompletedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeacherExamCompletedAdapter.ViewHolder {
        val binding: ItemTeacherExamCompletedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_teacher_exam_completed,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvSubjectName.text = subjectName
                binding.tvStudentName.text = studentName
                binding.tvExamType.text = "examType"
                binding.tvTotalQuestions.text = totalQuestions.toString()
                binding.tvUnSolvedQues.text = (totalQuestions - solved).toString()
                binding.tvTotalMarks.text = totalMarks.toString()
                binding.tvObtainedMarks.text = obtainMarks.toString()

                Glide.with(context).load(studentAvatarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                binding.tvDate.text = UtilsFunctions().getDDMMMYYYY(examDate)
                binding.tvStartTime.text = UtilsFunctions().getHHMMA(startTime)
                binding.tvEndTime.text = UtilsFunctions().getHHMMA(endTime)


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeacherExamCompletedBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}