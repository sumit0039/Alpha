package com.softwill.alpha.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemChatBinding


class ChatAdapter(
    private val mList: ArrayList<ChatItemModel>,
    private val context: Context,
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemChatBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chat,
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
                binding.tvTime.text = time

                if (unSeen) binding.cardCount.visibility = View.VISIBLE
                if (!unSeen) binding.cardCount.visibility = View.INVISIBLE

                binding.tvCount.text = unSeenCount.toString()

                holder.itemView.setOnClickListener {
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


    inner class ViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)


}