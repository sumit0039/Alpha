package com.softwill.alpha.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.databinding.ItemShareBinding
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel


class ShareAdapter(
    private var mList: ArrayList<ConnectionListModel>,
    private val context: Context,
) : RecyclerView.Adapter<ShareAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemShareBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_share, parent, false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvName.text = name
                binding.tvUsername.text = userName


                Glide.with(context).load(avtarUrl)
                    .placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                binding.btnShare.setOnClickListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("mName", name)
                    context?.startActivity(intent)
                }


            }


        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemShareBinding) : RecyclerView.ViewHolder(binding.root)


    /* fun removeItem(position: Int) {
         mList.removeAt(position)
         notifyItemRemoved(position)
         notifyItemRangeChanged(position, mList.size)
         notifyDataSetChanged()
     }*/

    fun filterList(filteredList: ArrayList<ConnectionListModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }



}