package com.softwill.alpha.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemCommentsBinding
import com.softwill.alpha.home.model.CommentModel
import com.softwill.alpha.utils.UtilsFunctions


class CommentAdapter(
    private val mList: ArrayList<CommentModel>,
    private val context: Context,
    private val callbackInterface: CommentCallbackInterface
) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommentsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_comments,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvName.text = name
                binding.tvComment.text = comment

                binding.tvTime.text = UtilsFunctions().getTimeAgoAndConvertToTimeZone(createdAt)


                binding.ivDelete.isVisible = isMyComment == 1


                if (!user.avtarUrl.isNullOrEmpty()) {
                    Glide.with(context).load(user.avtarUrl)
                        .placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)
                }

                binding.ivDelete.setOnClickListener {
                    callbackInterface.deleteCommentCallback(id, position)
                }


            }
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views
    //  inner class ViewHolder(binding: ItemHomePostBinding) : RecyclerView.ViewHolder(binding.root)


    inner class ViewHolder(val binding: ItemCommentsBinding) : RecyclerView.ViewHolder(binding.root)


    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }


    interface CommentCallbackInterface {
        fun deleteCommentCallback(commentId: Int , position: Int)
    }
}