package com.softwill.alpha.career.career_guidance.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.activity.CareerGuidanceDetailActivity
import com.softwill.alpha.career.career_guidance.model.CareerGuidanceModel
import com.softwill.alpha.databinding.ItemCareerGuidanceBinding


class CareerGuidanceAdapter(
    private val mList: ArrayList<CareerGuidanceModel>,
    private val context: Context
) :
    RecyclerView.Adapter<CareerGuidanceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemCareerGuidanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_career_guidance,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvFacultyName.text = facultyName
                binding.tvStreamName.text = streamName

                holder.binding.btnView.setOnClickListener {
                    val intent = Intent(context, CareerGuidanceDetailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mName", streamName)
                    intent.putExtra("mId", id)
                    context.startActivity(intent)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCareerGuidanceBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

}