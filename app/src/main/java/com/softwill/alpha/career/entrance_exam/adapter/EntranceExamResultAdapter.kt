package com.softwill.alpha.career.entrance_exam.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.career.entrance_exam.activity.EntranceExamDetailActivity
import com.softwill.alpha.career.entrance_exam.model.EntranceExamModel
import com.softwill.alpha.databinding.ItemEntranceExam3Binding


class EntranceExamResultAdapter(
    private var mList: ArrayList<EntranceExamModel>,
    private val context: Context,
) :
    RecyclerView.Adapter<EntranceExamResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemEntranceExam3Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_entrance_exam3,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvExamName.text = examName
                binding.tvExamDesc.text = examDesc

                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_no_image).into(binding.ivCover)

                binding.btnView.setOnClickListener {
                    val intent = Intent(context, EntranceExamDetailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mExamName", examName)
                    intent.putExtra("mExamId", id)
                    context.startActivity(intent)
                }

            }


        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemEntranceExam3Binding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}