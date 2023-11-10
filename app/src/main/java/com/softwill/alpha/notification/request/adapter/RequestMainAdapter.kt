package com.softwill.alpha.notification.request.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemRequestMainBinding
import com.softwill.alpha.notification.request.model.RequestMainModel
import com.softwill.alpha.utils.UtilsFunctions


class RequestMainAdapter(
    private val mList: ArrayList<RequestMainModel>,
    private val context: Context,
    private val callbackInterface: RequestSubAdapter.CallbackInterface,
) :
    RecyclerView.Adapter<RequestMainAdapter.ViewHolder>()  {

    lateinit var mRequestSubAdapter: RequestSubAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemRequestMainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_request_main,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvDate.text = UtilsFunctions().getDDMMMYYYY(date)


                mRequestSubAdapter = RequestSubAdapter(requests, context, callbackInterface)
                binding.rvRequestSub.adapter = mRequestSubAdapter
                mRequestSubAdapter.notifyDataSetChanged()

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemRequestMainBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }



}