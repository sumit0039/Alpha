package com.softwill.alpha.notification.request.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemRequestSubBinding
import com.softwill.alpha.notification.request.model.RequestModel
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity


class RequestSubAdapter(
    private val mList: ArrayList<RequestModel>,
    private val context: Context,
    private val callbackInterface: CallbackInterface,
) :
    RecyclerView.Adapter<RequestSubAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemRequestSubBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_request_sub,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvName.text = senderName

                if(!sender.avtarUrl.isNullOrEmpty()){
                    Glide.with(context).load(sender.avtarUrl)
                        .placeholder(R.drawable.icon_no_image).into(binding.ivImage)
                }


                binding.ivImage.setOnClickListener {
                    val intent = Intent(context, ProfileGuestActivity::class.java)
                    intent.putExtra("mVisitor", true)
                    intent.putExtra("mUserId", mList[position].senderUserId)
                    context.startActivity(intent)
                }

                binding.tvName.setOnClickListener {
                    val intent = Intent(context, ProfileGuestActivity::class.java)
                    intent.putExtra("mVisitor", true)
                    intent.putExtra("mUserId", mList[position].senderUserId)
                    context.startActivity(intent)
                }

                binding.cardAccept.setOnClickListener {
                    callbackInterface.connectionAcceptRejectCallback(id, "accept" , position)
                }

                binding.cardReject.setOnClickListener {
                    callbackInterface.connectionAcceptRejectCallback(id, "reject", position)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemRequestSubBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface CallbackInterface {
        fun connectionAcceptRejectCallback(id: Int, type: String, position: Int)
    }

}