package com.softwill.alpha.institute.culture.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemSponsorsBinding
import com.softwill.alpha.institute.culture.model.CultureSponsorsModel


class SponsorsAdapter(
    private val context: Context,
    private var mList: ArrayList<CultureSponsorsModel>,
) :
    RecyclerView.Adapter<SponsorsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SponsorsAdapter.ViewHolder {
        val binding: ItemSponsorsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_sponsors,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_no_image)
                    .into(binding.ivImage)

                binding.tvName.text = name
                binding.tvMobile.text = mobile
                binding.tvAmount.text = amount
                binding.tvTitle.text = "Sponsor for .... $sponserFor"
                binding.tvDesc.text = desc


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemSponsorsBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}