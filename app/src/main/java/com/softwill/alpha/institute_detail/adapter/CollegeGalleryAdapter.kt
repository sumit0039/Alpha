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
import com.softwill.alpha.databinding.ItemActivityPostBinding
import com.softwill.alpha.databinding.ItemSelectedStudentBinding
import com.softwill.alpha.institute_detail.model.GalleriesResponseItem
import com.softwill.alpha.institute_detail.model.placement.PlacementStudentsResponseItem


class CollegeGalleryAdapter(private var mList: ArrayList<GalleriesResponseItem>,
                            private val context: Context) :
    RecyclerView.Adapter<CollegeGalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemActivityPostBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_activity_post,
                parent,
                false
            )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(mList[position]){
                Glide.with(context).load(photoPath).error(R.drawable.icon_avatar).into(binding.image)
            }
        }

    }

    override fun getItemCount(): Int {
        //return mList.size
        return mList.size
    }

    class ViewHolder(val binding: ItemActivityPostBinding)
        : RecyclerView.ViewHolder(binding.root) {

    }
}