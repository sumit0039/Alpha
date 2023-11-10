package com.softwill.alpha.institute.online_exam.teacher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemClassesBinding
import com.softwill.alpha.institute.online_exam.teacher.model.ExamType


class ExamTypeAdapter(
    private val context: Context,
    private var mList: ArrayList<ExamType>,
    private val callbackInterface: ExamTypeAdapterCallbackInterface,
    var clickPosition: Int = 0,

    ) :
    RecyclerView.Adapter<ExamTypeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemClassesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_classes,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvTitle.text = name


                if (position == clickPosition) {
                    binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                } else {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey_light2
                        )
                    )
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3)
                }

                binding.root.setOnClickListener {
                    callbackInterface.examTypeClickCallback(id, position)
                    if (clickPosition != holder.adapterPosition) {
                        clickPosition = holder.adapterPosition
                    }
                    notifyDataSetChanged()
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface ExamTypeAdapterCallbackInterface {
        fun examTypeClickCallback(examId: Int, position: Int)
    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}