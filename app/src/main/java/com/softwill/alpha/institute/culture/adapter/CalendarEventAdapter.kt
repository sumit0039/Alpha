package com.softwill.alpha.institute.culture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemCalenderEventBinding
import com.softwill.alpha.institute.culture.model.CultureCalendarProgram
import com.softwill.alpha.utils.UtilsFunctions


class CalendarEventAdapter(
    private val context: Context,
    private var mList: ArrayList<CultureCalendarProgram>,
) :
    RecyclerView.Adapter<CalendarEventAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CalendarEventAdapter.ViewHolder {
        val binding: ItemCalenderEventBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_calender_event,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvTitle.text = title
                binding.tvDesc.text = desc

                binding.tvTime.text = UtilsFunctions().getHHMMA(startTime) + " to " + UtilsFunctions().getHHMMA(endTime)

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCalenderEventBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}