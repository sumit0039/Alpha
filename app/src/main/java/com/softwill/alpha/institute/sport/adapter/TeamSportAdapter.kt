package com.softwill.alpha.institute.sport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeamCultureBinding
import com.softwill.alpha.institute.sport.model.SportTeamMember


class TeamSportAdapter(
    private val context: Context,
    private var mList: ArrayList<SportTeamMember>,
) :
    RecyclerView.Adapter<TeamSportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeamSportAdapter.ViewHolder {
        val binding: ItemTeamCultureBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_team_culture,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(picUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivProfileImage)

                binding.tvName.text = name
                binding.tvMobile.text = mobile
                binding.tvPosition.text = position

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeamCultureBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}