package com.softwill.alpha.profile.privacy.blockedPeople

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemBlockedPeopleBinding


class BlockedPeopleAdapter(
    private val mList: ArrayList<BlockedUserResponse>,
    private val context: Context,
    private val callbackInterface: CallbackInterface
) :
    RecyclerView.Adapter<BlockedPeopleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BlockedPeopleAdapter.ViewHolder {
        val binding: ItemBlockedPeopleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_blocked_people,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvName.text = blockUser.name

                binding.tvUnblock.setOnClickListener {
                    callbackInterface.passResultCallback(position, blockUserId)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemBlockedPeopleBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface CallbackInterface {
        fun passResultCallback(position: Int, blockUserId: Int)
    }

}