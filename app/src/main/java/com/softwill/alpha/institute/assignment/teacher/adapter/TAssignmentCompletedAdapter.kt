package com.softwill.alpha.institute.assignment.teacher.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeacherAssignmentCompletedBinding
import com.softwill.alpha.institute.assignment.teacher.Model.TeacherCompletedAssignment
import com.softwill.alpha.institute.assignment.teacher.activity.CheckAssignmentActivity
import com.softwill.alpha.utils.UtilsFunctions


class TAssignmentCompletedAdapter(
    private val context: Context,
    private var mList: ArrayList<TeacherCompletedAssignment>,
) :
    RecyclerView.Adapter<TAssignmentCompletedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TAssignmentCompletedAdapter.ViewHolder {
        val binding: ItemTeacherAssignmentCompletedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_teacher_assignment_completed,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(studentAvtarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                binding.tvStudentName.text = studentName
                binding.tvRollNo.text = rollNumber
                binding.tvStartDate.text = UtilsFunctions().getDDMMMYYYY(startDate)
                binding.tvEndDate.text = UtilsFunctions().getDDMMMYYYY(endDate)


                binding.tvTotalMarks.text = totalMarks.toString()
                binding.tvObtainedMarks.text = obtainMarks.toString()

                if (status == 1 ) {
                    holder.binding.tvCheck.visibility = View.VISIBLE
                    holder.binding.tvObtainedMarks.visibility = View.GONE
                    holder.binding.textView33.visibility = View.GONE
                } else {
                    holder.binding.tvCheck.visibility = View.GONE
                    holder.binding.tvObtainedMarks.visibility = View.VISIBLE
                    holder.binding.textView33.visibility = View.VISIBLE
                }



                holder.binding.tvCheck.setOnClickListener {
                    val intent = Intent(context, CheckAssignmentActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mExamId", id)
                    context.startActivity(intent)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeacherAssignmentCompletedBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}