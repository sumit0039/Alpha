package com.softwill.alpha.institute.sport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemFacilities3Binding
import com.softwill.alpha.institute.canteen.adapter.FacilityAdapter
import com.softwill.alpha.institute.sport.model.SportAccessories


class AccessoriesAdapter(
    private val context: Context,
    private var mList: ArrayList<SportAccessories>,
) :
    RecyclerView.Adapter<AccessoriesAdapter.ViewHolder>() {

    var mFacilityAdapter: FacilityAdapter? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AccessoriesAdapter.ViewHolder {
        val binding: ItemFacilities3Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_facilities3,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvTitle.text = "Sport Accessories"
                binding.tvDescription.text = desc


                mFacilityAdapter = FacilityAdapter(context, accessories)
                binding.rvFacility.adapter = mFacilityAdapter
                mFacilityAdapter!!.notifyDataSetChanged()


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacilities3Binding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}