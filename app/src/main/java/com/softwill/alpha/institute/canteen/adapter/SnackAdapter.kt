package com.softwill.alpha.institute.canteen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemMenuBinding
import com.softwill.alpha.institute.canteen.model.CanteenMenuCard


class SnackAdapter(
    private var mList: ArrayList<CanteenMenuCard>, private val context: Context,
) :
    RecyclerView.Adapter<SnackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SnackAdapter.ViewHolder {
        val binding: ItemMenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_menu,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(itemAvtarUrl).placeholder(R.drawable.icon_no_image).into(binding.ivImage)

                binding.tvName.text = itemName
                binding.tvPrice.text = price.toString()

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

}