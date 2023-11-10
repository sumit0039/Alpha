package com.softwill.alpha.profile.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemActivityBinding


class ActivityAssignmentsAdapter(
    private val mList: ArrayList<ActivityItemModel>,
    private val context: Context
) :
    RecyclerView.Adapter<ActivityAssignmentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ActivityAssignmentsAdapter.ViewHolder {
        val binding: ItemActivityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_activity,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvAssignmentsName.text = name


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemActivityBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

}