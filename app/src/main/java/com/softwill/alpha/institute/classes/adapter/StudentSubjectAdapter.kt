package com.softwill.alpha.institute.classes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemStudentSubjectBinding
import com.softwill.alpha.institute.classes.model.ClassSubject


class StudentSubjectAdapter(
    private val context: Context,
    private val mList: ArrayList<ClassSubject>,
) :
    RecyclerView.Adapter<StudentSubjectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): StudentSubjectAdapter.ViewHolder {
        val binding: ItemStudentSubjectBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_student_subject,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvSubjectName.text = subjectName

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemStudentSubjectBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}