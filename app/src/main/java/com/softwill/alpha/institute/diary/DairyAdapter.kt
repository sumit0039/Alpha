package com.softwill.alpha.institute.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemDiaryBinding
import com.softwill.alpha.utils.UtilsFunctions


class DairyAdapter(
    private val mList: ArrayList<DairyModel>,
    private val context: Context,
    private val callbackInterface: DiaryAdapterCallbackInterface
) :
    RecyclerView.Adapter<DairyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): DairyAdapter.ViewHolder {
        val binding: ItemDiaryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_diary,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvDay.text = UtilsFunctions().getDD(createdAt)
                binding.tvMonth.text = UtilsFunctions().getMMM(createdAt)

                
                binding.tvTitle.text = title
                binding.tvDesc.text = desc




                holder.itemView.setOnClickListener {
                    callbackInterface.detailsCallback(position, id, title, desc)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface DiaryAdapterCallbackInterface {
        fun detailsCallback(position: Int, id: Int, title: String, desc: String)
    }
}