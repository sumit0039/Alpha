package com.softwill.alpha.institute.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemAttendanceTeacherBinding


class AttendanceTeacherAdapter(
    private val context: Context,
) :
    RecyclerView.Adapter<AttendanceTeacherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AttendanceTeacherAdapter.ViewHolder {
        val binding: ItemAttendanceTeacherBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_attendance_teacher,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            /*with(mList[position]) {





            }*/
        }


    }

    override fun getItemCount(): Int {
        return 3
    }


    inner class ViewHolder(val binding: ItemAttendanceTeacherBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}