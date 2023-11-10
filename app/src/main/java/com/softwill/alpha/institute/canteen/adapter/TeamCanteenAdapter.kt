package com.softwill.alpha.institute.canteen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTeamCanteenBinding
import com.softwill.alpha.institute.canteen.model.CanteenTeamMember


class TeamCanteenAdapter(
    private val context: Context,
    private var mList: ArrayList<CanteenTeamMember>,
) :
    RecyclerView.Adapter<TeamCanteenAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TeamCanteenAdapter.ViewHolder {
        val binding: ItemTeamCanteenBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_team_canteen,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(avatarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivImage)

                binding.tvName.text = name

                binding.tvPosition.text = position
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTeamCanteenBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}