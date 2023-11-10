package com.softwill.alpha.institute.sport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemOpportunityBinding
import com.softwill.alpha.institute.sport.model.SportOpportunity


class OpportunityAdapter(
    private val context: Context,
    private var mList: ArrayList<SportOpportunity>,
) :
    RecyclerView.Adapter<OpportunityAdapter.ViewHolder>() {

    var mCandidateAdapter: CandidateAdapter? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OpportunityAdapter.ViewHolder {
        val binding: ItemOpportunityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_opportunity,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvTitle.text = title
                binding.tvDescription.text = desc


                mCandidateAdapter = CandidateAdapter(context, candidates)
                binding.rvCandidates.adapter = mCandidateAdapter
                mCandidateAdapter!!.notifyDataSetChanged()
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemOpportunityBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}