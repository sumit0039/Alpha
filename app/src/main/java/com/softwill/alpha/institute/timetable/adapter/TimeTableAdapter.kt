package com.softwill.alpha.institute.timetable.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTimeTableBinding
import com.softwill.alpha.institute.timetable.model.TimeTableModel
import com.softwill.alpha.utils.UtilsFunctions


class TimeTableAdapter(
    private val mList: ArrayList<TimeTableModel>, private val context: Context,
    private val callbackInterface: TimeTableAdapter.TimeTableAdapterCallbackInterface,
    private val IsStudentLogin: Boolean

) :
    RecyclerView.Adapter<TimeTableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemTimeTableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_time_table,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvSubjectName.text = subjectName
                binding.tvDesc.text = ""
                binding.tvStartTime.text = UtilsFunctions().getHHMMA(startTime)
                binding.tvEndTime.text = UtilsFunctions().getHHMMA(endTime)

                holder.itemView.setOnClickListener {
                    //callbackInterface.detailsCallback(position)
                }

                if(!IsStudentLogin){
                    binding.ibDelete.visibility = View.VISIBLE
                }else{
                    binding.ibDelete.visibility = View.GONE
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTimeTableBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface TimeTableAdapterCallbackInterface {
        fun detailsCallback(position: Int)
    }
}