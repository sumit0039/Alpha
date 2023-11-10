package com.softwill.alpha.institute.assignment.student.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemStudentAssignmentOngoingBinding
import com.softwill.alpha.institute.assignment.student.activity.AssignmentQuestionsActivity
import com.softwill.alpha.institute.assignment.student.model.StudentOngoingAssignment
import com.softwill.alpha.utils.UtilsFunctions


class SAssignmentOngoingAdapter(
    private val context: Context,
    private var mList: ArrayList<StudentOngoingAssignment>,
) :
    RecyclerView.Adapter<SAssignmentOngoingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SAssignmentOngoingAdapter.ViewHolder {
        val binding: ItemStudentAssignmentOngoingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_student_assignment_ongoing,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvSubjectName.text = subjectName
                binding.tvTeacherName.text = teacherName
                binding.tvStartDate.text = UtilsFunctions().getDDMMMYYYY(startDate)
                binding.tvEndDate.text = UtilsFunctions().getDDMMMYYYY(endDate)



                Glide.with(context).load(teacherAvtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)


                holder.itemView.setOnClickListener {
                    val intent = Intent(context, AssignmentQuestionsActivity::class.java)
                    intent.putExtra("mExamId", id)
                    intent.putExtra("mSubjectName", subjectName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    context.startActivity(intent)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemStudentAssignmentOngoingBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}