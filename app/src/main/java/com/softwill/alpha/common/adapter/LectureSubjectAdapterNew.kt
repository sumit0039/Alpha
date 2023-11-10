package com.softwill.alpha.common.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.common.model.LectureSubjectModel
import com.softwill.alpha.databinding.ItemClassesBinding


class LectureSubjectAdapterNew(
    private val context: Context,
    private var mList: ArrayList<LectureSubjectModel>,
    private val callbackInterface: LectureSubjectCallbackInterface,
    var clickPosition: Int = 0,
) :
    RecyclerView.Adapter<LectureSubjectAdapterNew.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemClassesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_classes,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

            binding.tvTitle.text = subjectName


                if (position == clickPosition) {
                    binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                } else {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey_light2
                        )
                    )
                    binding.tvTitle.setBackgroundResource(R.drawable.bg_rounded_3)
                }

                binding.root.setOnClickListener {
                    callbackInterface.lectureSubjectClickCallback(subjectId, position)
                    if (clickPosition != holder.adapterPosition) {
                        clickPosition = holder.adapterPosition
                    }
                    notifyDataSetChanged()
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface LectureSubjectCallbackInterface {
        fun lectureSubjectClickCallback(subjectId: Int, position: Int)
    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}