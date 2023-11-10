package com.softwill.alpha.institute.report_card.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemReportCardTeacherBinding
import com.softwill.alpha.institute.classes.model.StudentInfo


class ReportCardTeacherAdapter(
    private var mList: ArrayList<StudentInfo>,
    private val context: Context,
    private val callbackInterface: ReportCardAdapterCallbackInterface
) :
    RecyclerView.Adapter<ReportCardTeacherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ReportCardTeacherAdapter.ViewHolder {
        val binding: ItemReportCardTeacherBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_report_card_teacher,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvName.text = "$firstName $lastName"
                binding.tvUserName.text = userName

                Glide.with(context).load(User.avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                binding.tvAdd.setOnClickListener{
                    callbackInterface.callback(position, "$firstName $lastName", studentId)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemReportCardTeacherBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface ReportCardAdapterCallbackInterface {
        fun callback(position: Int, name: String, studentId: Int)
    }

    fun updateData(newStudents: ArrayList<StudentInfo>) {
        mList = newStudents
        notifyDataSetChanged()
    }
}