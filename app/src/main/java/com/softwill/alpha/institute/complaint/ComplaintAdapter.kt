package com.softwill.alpha.institute.complaint

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemComplaintBinding
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.setResizableText


class ComplaintAdapter(
    private val context: Context,
    private val mList: ArrayList<ComplaintModel>,
) :
    RecyclerView.Adapter<ComplaintAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ComplaintAdapter.ViewHolder {
        val binding: ItemComplaintBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_complaint,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvTitle.text = title
                binding.tvDescription.setResizableText(desc, 4, true)
                binding.tvDate.text = UtilsFunctions().getDDMMM(createdAt)






            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemComplaintBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}