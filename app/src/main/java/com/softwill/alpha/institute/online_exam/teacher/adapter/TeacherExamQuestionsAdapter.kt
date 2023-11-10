package com.softwill.alpha.institute.online_exam.teacher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.model.QuestionModel
import com.softwill.alpha.databinding.ItemExamQuestionBinding

class TeacherExamQuestionsAdapter(
    private val context: Context,
    private var mList: ArrayList<QuestionModel>,
) :
    RecyclerView.Adapter<TeacherExamQuestionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeacherExamQuestionsAdapter.ViewHolder {
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

                if (selectedAnswerId != 0) {

                    binding.root.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green
                        )
                    );
                } else {
                    binding.root.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorYellow
                        )
                    );
                }

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