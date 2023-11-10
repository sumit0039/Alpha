package com.softwill.alpha.institute.assignment.teacher.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeacherAssignmentOngoingBinding
import com.softwill.alpha.institute.assignment.teacher.Model.TeacherOngoingAssignment
import com.softwill.alpha.institute.assignment.teacher.activity.ViewQuestionsActivity
import com.softwill.alpha.utils.UtilsFunctions


class TAssignmentOngoingAdapter(
    private val context: Context,
    private var mList: ArrayList<TeacherOngoingAssignment>,
    private val callbackInterface: TAssignmentOngoingAdapterCallbackInterface
) :
    RecyclerView.Adapter<TAssignmentOngoingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TAssignmentOngoingAdapter.ViewHolder {
        val binding: ItemTeacherAssignmentOngoingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_teacher_assignment_ongoing,
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

                Glide.with(context).load(teacherAvatarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ViewQuestionsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mAssignmentId", id)
                    intent.putExtra("mForm", "Assignment")
                    context.startActivity(intent)
                }

                binding.ibDelete.setOnClickListener {
                    callbackInterface.deleteAssignmentCallback(position, id)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeacherAssignmentOngoingBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface TAssignmentOngoingAdapterCallbackInterface {
        fun deleteAssignmentCallback(position: Int, id: Int)
    }

}