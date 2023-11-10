package com.softwill.alpha.institute.assignment.student.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemStudentAssignmentCompletedBinding
import com.softwill.alpha.institute.assignment.student.model.StudentCompletedAssignment
import com.softwill.alpha.utils.UtilsFunctions


class SAssignmentCompletedAdapter(
    private val context: Context,
    private var mList: ArrayList<StudentCompletedAssignment>,
) :
    RecyclerView.Adapter<SAssignmentCompletedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SAssignmentCompletedAdapter.ViewHolder {
        val binding: ItemStudentAssignmentCompletedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_student_assignment_completed,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(teacherAvatarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)
                binding.tvSubjectName.text = subjectName
                binding.tvTeacherName.text = teacherName
                binding.tvTotalMarks.text = totalMarks.toString()
                binding.tvObtainedMarks.text = obtainMarks.toString()
                binding.tvStartDate.text = UtilsFunctions().getDDMMMYYYY(startDate)
                binding.tvEndDate.text = UtilsFunctions().getDDMMMYYYY(endDate)

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemStudentAssignmentCompletedBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}