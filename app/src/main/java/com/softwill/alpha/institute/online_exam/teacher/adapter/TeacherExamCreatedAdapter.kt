package com.softwill.alpha.institute.online_exam.teacher.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemOnlineExamCreatedBinding
import com.softwill.alpha.institute.assignment.teacher.activity.ViewQuestionsActivity
import com.softwill.alpha.institute.online_exam.teacher.model.TeacherOnlineExam
import com.softwill.alpha.utils.UtilsFunctions


class TeacherExamCreatedAdapter(
    private val context: Context,
    private var mList: ArrayList<TeacherOnlineExam>,
    private val callbackInterface: TeacherExamCreatedAdapterCallbackInterface

) :
    RecyclerView.Adapter<TeacherExamCreatedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeacherExamCreatedAdapter.ViewHolder {
        val binding: ItemOnlineExamCreatedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_online_exam_created,
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
                binding.tvExamType.text = examType
                binding.tvTotalQuestions.text = totalQuestions.toString()
                binding.tvTotalMarks.text = totalMarks.toString()

                Glide.with(context).load(teacherAvatarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                binding.tvDate.text = UtilsFunctions().getDDMMMYYYY(examDate)
                binding.tvStartTime.text = UtilsFunctions().getHHMMA(startTime)
                binding.tvEndTime.text = UtilsFunctions().getHHMMA(endTime)


                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ViewQuestionsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mOnlineExamId", id)
                    intent.putExtra("mForm", "OnlineExam")
                    context.startActivity(intent)
                }

                binding.ibDelete.setOnClickListener {
                    callbackInterface.deleteExamCallback(position, id)
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemOnlineExamCreatedBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }


    interface TeacherExamCreatedAdapterCallbackInterface {
        fun deleteExamCallback(position: Int, id: Int)
    }

}