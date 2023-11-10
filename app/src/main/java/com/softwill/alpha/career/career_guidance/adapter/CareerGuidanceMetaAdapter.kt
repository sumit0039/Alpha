package com.softwill.alpha.career.career_guidance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.MetaDataModel
import com.softwill.alpha.databinding.ItemCareerGuidanceMetaBinding


class CareerGuidanceMetaAdapter(
    private val mList: ArrayList<MetaDataModel>,
    private val context: Context,
    private val callbackInterface: CareerGuidanceMetaAdapterCallbackInterface,
) :
    RecyclerView.Adapter<CareerGuidanceMetaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemCareerGuidanceMetaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_career_guidance_meta,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvMetaTitle.text = title
                //binding.tvMetaDesc.text = desc
                binding.webview.loadData(desc, "text/html", "UTF-8")

                binding.rlTop.setOnClickListener {
                    callbackInterface.titleClickCallback(position)
                }

                if (isOpen) {
                    binding.webview.visibility = View.VISIBLE
                    binding.ivCard.setImageDrawable(context.resources.getDrawable(R.drawable.icon_minus))
                } else {
                    binding.webview.visibility = View.GONE
                    binding.ivCard.setImageDrawable(context.resources.getDrawable(R.drawable.icon_plus))
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCareerGuidanceMetaBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    interface CareerGuidanceMetaAdapterCallbackInterface {
        fun titleClickCallback(position: Int)
    }
}