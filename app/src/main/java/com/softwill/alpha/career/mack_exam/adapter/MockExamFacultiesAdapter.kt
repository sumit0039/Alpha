package com.softwill.alpha.career.mack_exam.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.databinding.ItemBestCollegeCategoryBinding


class MockExamFacultiesAdapter(
    private var mList: ArrayList<FacultyModel2>,
    private val context: Context,
    private val callbackInterface: MockExamFacultiesCallbackInterface,
    var clickPosition: Int = 0,

    ) :
    RecyclerView.Adapter<MockExamFacultiesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemBestCollegeCategoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_best_college_category,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.btnTitle.text = name


                if (position == clickPosition) {
                    binding.btnTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.btnTitle.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                } else {
                    binding.btnTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey_light2
                        )
                    )
                    binding.btnTitle.setBackgroundResource(R.drawable.bg_rounded_3)
                }

                binding.root.setOnClickListener {
                    callbackInterface.itemClickCallback(facultyId, position)
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


    inner class ViewHolder(val binding: ItemBestCollegeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface MockExamFacultiesCallbackInterface {
        fun itemClickCallback(facultyId: Int, position: Int)
    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}