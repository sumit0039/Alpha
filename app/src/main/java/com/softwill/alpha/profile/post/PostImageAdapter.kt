package com.softwill.alpha.profile.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemPostImageBinding


class PostImageAdapter(
    private val mList: ArrayList<PostImageItemModel>,
    private val context: Context
) :
    RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PostImageAdapter.ViewHolder {
        val binding: ItemPostImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_post_image,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {


            with(mList[position]) {
                Glide.with(context).load(Image).placeholder(R.drawable.icon_no_image)
                    .into(binding.image)

                binding.ivCancel.setOnClickListener {
                    removeItem(position)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

}