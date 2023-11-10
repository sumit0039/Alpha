package com.softwill.alpha.institute.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemBestCollegeCategoryBinding
import com.softwill.alpha.institute.library.Model.BookCategories


class BookCategoryAdapter(
    private var mList: ArrayList<BookCategories>,
    private val context: Context,
    private val callbackInterface: BookCategoryCallbackInterface,
    var clickPosition: Int = 0,
) :
    RecyclerView.Adapter<BookCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemBestCollegeCategoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.softwill.alpha.R.layout.item_best_college_category,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
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
                    callbackInterface.itemClickCallback(categoryId, position)
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


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPosition() {
        clickPosition = 0
        notifyDataSetChanged()
    }

    //Filter New
    /* fun filterList(filteredList: ArrayList<BestCollegeItemModel>) {
         mList = filteredList
         notifyDataSetChanged()
     }*/

    interface BookCategoryCallbackInterface {
        fun itemClickCallback(categoryId: Int, position: Int)
    }
}