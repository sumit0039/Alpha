package com.softwill.alpha.institute.culture.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemGalleryBinding
import com.softwill.alpha.institute.culture.model.PhotoModel


class GalleryAdapter(
    private val context: Context,
    private val callbackInterface: CallbackInterface,
    private var mList: ArrayList<PhotoModel>,
) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): GalleryAdapter.ViewHolder {
        val binding: ItemGalleryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_gallery,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.ivImage.setOnClickListener {
                    if (picUrl != null){

                        callbackInterface.onImageCallback(position, picUrl)
                    }
                }

                Glide.with(context).load(picUrl).placeholder(R.drawable.icon_no_image)
                    .into(binding.ivImage)


            }


        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface CallbackInterface {
        fun onImageCallback(position: Int, picUrl: String)
    }
    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}