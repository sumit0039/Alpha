package com.softwill.alpha.institute.classes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemSubjectBinding
import com.softwill.alpha.institute.classes.model.StudentSubject


class ClassSubjectAdapter(
    private val context: Context,
    private var mList: ArrayList<StudentSubject>,
) :
    RecyclerView.Adapter<ClassSubjectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ClassSubjectAdapter.ViewHolder {
        val binding: ItemSubjectBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_subject,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvSubjectName.text = subjectName
                binding.tvTeacherName.text = assignTeacher
                binding.tvUserName.text = userName

                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}