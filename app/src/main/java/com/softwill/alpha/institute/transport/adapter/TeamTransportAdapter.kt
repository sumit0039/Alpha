package com.softwill.alpha.institute.transport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeamTransportBinding
import com.softwill.alpha.institute.transport.model.TransportTeamMember


class TeamTransportAdapter(
    private val context: Context,
    private var mList: ArrayList<TransportTeamMember>,
) :
    RecyclerView.Adapter<TeamTransportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeamTransportAdapter.ViewHolder {
        val binding: ItemTeamTransportBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_team_transport,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                binding.tvName.text = name
                binding.tvMobile.text = mobile
                binding.tvEmail.text = email

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeamTransportBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}