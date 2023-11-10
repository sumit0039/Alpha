package com.softwill.alpha.institute.canteen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemFacilityBinding


class FacilityAdapter(
    private val context: Context,
    private var mList: ArrayList<String>,
) :
    RecyclerView.Adapter<FacilityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FacilityAdapter.ViewHolder {
        val binding: ItemFacilityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_facility,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvName.text = mList[position]

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacilityBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}