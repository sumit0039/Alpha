package com.softwill.alpha.institute.report_card.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemReportCardStudentBinding
import com.softwill.alpha.institute.report_card.model.ReportCard


class ReportCardStudentAdapter(
    private val context: Context,
    private var mList: ArrayList<ReportCard>,
) :
    RecyclerView.Adapter<ReportCardStudentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ReportCardStudentAdapter.ViewHolder {
        val binding: ItemReportCardStudentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_report_card_student,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {
                Glide.with(context).load(reportCardUrl).placeholder(R.drawable.no_best_college).into(binding.ivImage)
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemReportCardStudentBinding) :
        RecyclerView.ViewHolder(binding.root)


   /* fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/


}