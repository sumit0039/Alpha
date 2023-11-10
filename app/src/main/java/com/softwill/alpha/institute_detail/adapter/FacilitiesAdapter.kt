package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.institute_detail.model.FacilitiesItemModel
import com.softwill.alpha.databinding.ItemFacilitiesBinding


class FacilitiesAdapter(
    private var mList: ArrayList<FacilitiesItemModel>,
    private val context: Context
) :
    RecyclerView.Adapter<FacilitiesAdapter.ViewHolder>() {

    lateinit var mFacilities2Adapter: Facilities2Adapter

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacilitiesBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_facilities,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvName.text = name

                mFacilities2Adapter = Facilities2Adapter( context)
                binding.rvFacilities2.adapter = mFacilities2Adapter
                mFacilities2Adapter.notifyDataSetChanged()



            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacilitiesBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    //Filter New
    fun filterList(filteredList: ArrayList<FacilitiesItemModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }
}