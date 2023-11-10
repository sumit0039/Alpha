package com.softwill.alpha.institute.transport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemFeesBinding
import com.softwill.alpha.institute.transport.model.TransportFees


class FeesAdapter(
    private val context: Context,
    private var mList: ArrayList<TransportFees>,
) :
    RecyclerView.Adapter<FeesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FeesAdapter.ViewHolder {
        val binding: ItemFeesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_fees,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvLocation.text = "$fromLocation to $toLocation"
                binding.tvFees.text = fees.toString()
                binding.tvDesc.text = desc

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFeesBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}