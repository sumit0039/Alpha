package com.softwill.alpha.institute.assignment.student.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemExamQuestionBinding
import com.softwill.alpha.institute.assignment.student.model.AssignmentQuestionModel

class StudentAssignmentQuestionsAdapter(
    private val context: Context,
    private var mList: ArrayList<AssignmentQuestionModel>,
) :
    RecyclerView.Adapter<StudentAssignmentQuestionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): StudentAssignmentQuestionsAdapter.ViewHolder {
        val binding: ItemExamQuestionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_exam_question,
            parent,
            false
        )
        return ViewHolder(binding)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvNo.text = "${position + 1}"


                val backgroundColor = if (questionType == 1 && selectedAnswerId != 0 ||
                    questionType == 2 && !selectedAnswer.isNullOrEmpty()) {
                    ContextCompat.getColor(context, R.color.green)
                } else {
                    ContextCompat.getColor(context, R.color.colorYellow)
                }
                binding.root.setCardBackgroundColor(backgroundColor)

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemExamQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}