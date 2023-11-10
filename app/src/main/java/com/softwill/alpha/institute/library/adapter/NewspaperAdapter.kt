package com.softwill.alpha.institute.library.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemLibraryNewspaperBinding
import com.softwill.alpha.institute.library.Model.NewsPaper
import com.softwill.alpha.institute.library.activity.ReadBookActivity


class NewspaperAdapter(
    private val context: Context,
    private var mList: ArrayList<NewsPaper>,

    ) :
    RecyclerView.Adapter<NewspaperAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NewspaperAdapter.ViewHolder {
        val binding: ItemLibraryNewspaperBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_library_newspaper,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(thumbnailUrl).placeholder(R.drawable.icon_no_image)
                    .into(binding.ivImage)

                binding.tvTitle.text = name



                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ReadBookActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mScreenTitle", "Read Newspsaper")
                    intent.putExtra("mBookId", -1)
                    intent.putExtra("mThumbnailUrl", thumbnailUrl)
                    intent.putExtra("mPdfUrl", paperUrl)
                    intent.putExtra("mName", name)
                    intent.putExtra("mWriterName", "")
                    context.startActivity(intent)
                }


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemLibraryNewspaperBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}