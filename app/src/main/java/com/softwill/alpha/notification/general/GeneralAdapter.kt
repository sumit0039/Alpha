package com.softwill.alpha.notification.general

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemGeneralBinding


class GeneralAdapter(private val context: Context) :
    RecyclerView.Adapter<GeneralAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): GeneralAdapter.ViewHolder {
        val binding: ItemGeneralBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_general,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            /* with(mList[position]) {


             }*/
        }


    }

    override fun getItemCount(): Int {
        return 22
    }


    inner class ViewHolder(val binding: ItemGeneralBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}