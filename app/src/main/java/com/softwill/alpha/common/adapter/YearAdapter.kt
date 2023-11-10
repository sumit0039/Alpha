package com.softwill.alpha.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemClassesBinding


class YearAdapter(
    private val context: Context,
) :
    RecyclerView.Adapter<YearAdapter.ViewHolder>() {
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
            /*with(mList[position]) {*/

            binding.tvTitle.text = "202${position}"

            if(position == 0){
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            }

            /*}*/
        }


    }

    override fun getItemCount(): Int {
        return 8
    }


    inner class ViewHolder(val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}