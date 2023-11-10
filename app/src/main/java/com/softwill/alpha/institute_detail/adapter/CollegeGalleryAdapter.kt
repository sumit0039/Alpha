package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R


class CollegeGalleryAdapter(private val context: Context) :
    RecyclerView.Adapter<CollegeGalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity_post,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.image.setImageResource(R.drawable.icon_avatar)

    }

    override fun getItemCount(): Int {
        //return mList.size
        return 17
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val image: ImageView = itemView.findViewById(R.id.image)

    }
}