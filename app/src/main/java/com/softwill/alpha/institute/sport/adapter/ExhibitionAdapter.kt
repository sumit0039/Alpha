package com.softwill.alpha.institute.sport.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemExhibitioinBinding
import com.softwill.alpha.institute.sport.activity.ParticipantActivity
import com.softwill.alpha.institute.sport.model.SportExhibitions


class ExhibitionAdapter(
    private val context: Context,
    private var mList: ArrayList<SportExhibitions>,
) :
    RecyclerView.Adapter<ExhibitionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ExhibitionAdapter.ViewHolder {
        val binding: ItemExhibitioinBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_exhibitioin,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                binding.tvName.text = studentName
                binding.tvTitle.text = title



                binding.root.setOnClickListener{
                    val intent = Intent(context, ParticipantActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("itemData",  mList[position])
                    context.startActivity(intent)
                }



            }


        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemExhibitioinBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}