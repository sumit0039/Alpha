package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemEntranceExamBinding
import com.softwill.alpha.databinding.ItemOurPartnerBinding
import com.softwill.alpha.institute_detail.model.EntranceExamResponseItem
import com.softwill.alpha.institute_detail.model.placement.PlacementCompaniesResponseItem


class OurPartnerAdapter(
    private var mList: ArrayList<PlacementCompaniesResponseItem>,
    private val context: Context)
    : RecyclerView.Adapter<OurPartnerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemOurPartnerBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_our_partner,
                parent,
                false
            )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(mList[position]){
                Glide.with(context).load(companyLogo).error(R.drawable.icon_avatar).into(holder.binding.image)
                binding.txtName.text = companyName
            }
        }



    }

    override fun getItemCount(): Int {
        //return mList.size
        return mList.size
    }

    class ViewHolder(val binding: ItemOurPartnerBinding)
        : RecyclerView.ViewHolder(binding.root) {

    }
}