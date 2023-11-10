package com.softwill.alpha.profile.editProfile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemHobbiesSkillBinding


class SkillsAdapter(
    private val mList: MutableList<String> = mutableListOf(),
    private val context: Context,
    private val callbackInterface: SkillsCallbackInterface
) :
    RecyclerView.Adapter<SkillsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemHobbiesSkillBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_hobbies_skill,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.tvName.text = mList[position]


            binding.tvCancel.setOnClickListener {
                //removeItem(position)
                callbackInterface.deleteSkillsCallback(position)
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemHobbiesSkillBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface SkillsCallbackInterface {
        fun deleteSkillsCallback(position: Int)
    }

}