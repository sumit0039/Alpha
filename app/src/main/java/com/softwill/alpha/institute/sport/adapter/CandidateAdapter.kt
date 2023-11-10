package com.softwill.alpha.institute.sport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemCandidatesBinding
import com.softwill.alpha.institute.sport.model.Candidate


class CandidateAdapter(
    private val context: Context,
    private var mList: ArrayList<Candidate>,
) :
    RecyclerView.Adapter<CandidateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CandidateAdapter.ViewHolder {
        val binding: ItemCandidatesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_candidates,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvCount.text = (position + 1).toString() + "."
                binding.tvName.text = studentName

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCandidatesBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}