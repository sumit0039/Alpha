package com.softwill.alpha.common.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemExamQuestionBinding
import com.softwill.alpha.institute.assignment.teacher.Model.CreateQuestion

class PreviousQuestionsAdapter(
    private val context: Context,
    private var mList: ArrayList<CreateQuestion>,
) :
    RecyclerView.Adapter<PreviousQuestionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PreviousQuestionsAdapter.ViewHolder {
        val binding: ItemExamQuestionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_exam_question,
            parent,
            false
        )
        return ViewHolder(binding)


    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvNo.text = "${position + 1}"
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