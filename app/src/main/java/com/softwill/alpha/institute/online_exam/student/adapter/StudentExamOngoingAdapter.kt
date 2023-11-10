package com.softwill.alpha.institute.online_exam.student.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemOnlineExamCreatedBinding
import com.softwill.alpha.institute.online_exam.student.model.StudentOngoingExam
import com.softwill.alpha.utils.UtilsFunctions


class StudentExamOngoingAdapter(
    private val context: Context,
    private var mList: ArrayList<StudentOngoingExam>,
    private val callbackInterface: AdapterCallbackInterface
) :
    RecyclerView.Adapter<StudentExamOngoingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): StudentExamOngoingAdapter.ViewHolder {
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

                binding.ibDelete.visibility = View.GONE

                binding.tvSubjectName.text = subjectName
                binding.tvTeacherName.text = teacherName
                binding.tvExamType.text = examType
                binding.tvTotalQuestions.text = totalQuestions.toString()
                binding.tvTotalMarks.text = totalMarks.toString()

                com.bumptech.glide.Glide.with(context).load(teacherAvtarUrl).placeholder(com.softwill.alpha.R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                binding.tvDate.text = UtilsFunctions().getDDMMMYYYY(examDate)
                binding.tvStartTime.text = UtilsFunctions().getHHMMA(startTime)
                binding.tvEndTime.text = UtilsFunctions().getHHMMA(endTime)


                holder.itemView.setOnClickListener {
                    callbackInterface.onSolveCallback(position , id , subjectName)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemOnlineExamCreatedBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

    interface AdapterCallbackInterface {
        fun onSolveCallback(position: Int, examId: Int, subject: String)
    }
}