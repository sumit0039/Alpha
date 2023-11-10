package com.softwill.alpha.institute.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemSavedCategoryBinding
import com.softwill.alpha.institute.library.Model.SavedBookCategory


class SavedBookCategoryAdapter(
    private val context: Context,
    private var mList: ArrayList<SavedBookCategory>,
) :
    RecyclerView.Adapter<SavedBookCategoryAdapter.ViewHolder>() {

    var mSavedBookAdapter: SavedBookAdapter? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SavedBookCategoryAdapter.ViewHolder {
        val binding: ItemSavedCategoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_saved_category,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            with(mList[position]) {

                binding.tvCategoryName.text = name


                mSavedBookAdapter = SavedBookAdapter(context, Books)
                binding.rvSavedBook.adapter = mSavedBookAdapter
                mSavedBookAdapter!!.notifyDataSetChanged()

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemSavedCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}