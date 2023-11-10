package com.softwill.alpha.institute.classes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemPendingAssignmentBinding
import com.softwill.alpha.institute.classes.model.PendingAssignment
import com.softwill.alpha.utils.UtilsFunctions


class PendingAssignmentAdapter(
    private val context: Context,
    private val mList: ArrayList<PendingAssignment>,
) :
    RecyclerView.Adapter<PendingAssignmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PendingAssignmentAdapter.ViewHolder {
        val binding: ItemPendingAssignmentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_pending_assignment,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvExamName.text = subjectName
                binding.tvTotalMarks.text = totalMarks.toString()
                binding.tvDue.text = UtilsFunctions().getDDMMMYYYY(startDate)

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemPendingAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}