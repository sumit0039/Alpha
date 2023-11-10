package com.softwill.alpha.institute.classes.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemStudentBinding
import com.softwill.alpha.institute.classes.activity.StudentDetailsActivity
import com.softwill.alpha.institute.classes.model.StudentInfo


class StudentAdapter(
    private var mList: ArrayList<StudentInfo>,
    private val context: Context,
    private val  IsStudentLogin: Boolean,
) :
    RecyclerView.Adapter<StudentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): StudentAdapter.ViewHolder {
        val binding: ItemStudentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_student,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


            holder.itemView.setOnClickListener {
                if (!IsStudentLogin){
                    val intent = Intent(context, StudentDetailsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mName", "$firstName $lastName")
                    intent.putExtra("mUserName", userName)
                    intent.putExtra("mProfileImage", User.avtarUrl)
                    intent.putExtra("mStudentId", studentId)
                    intent.putExtra("mUserId", userId)
                    context.startActivity(intent)
                }

            }

                binding.tvName.text = "$firstName $lastName"
                binding.tvRollName.text = rollNumber.toString()
                binding.tvUserName.text = userName

                Glide.with(context).load(User.avtarUrl).placeholder(R.drawable.icon_no_image).into(binding.ivProfileImage)



            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun updateData(newStudents: ArrayList<StudentInfo>) {
        mList = newStudents
        notifyDataSetChanged()
    }
    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/



}