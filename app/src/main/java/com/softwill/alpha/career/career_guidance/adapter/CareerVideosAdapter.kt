package com.softwill.alpha.career.career_guidance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.VideoModel


class CareerVideosAdapter(
    private val context: Context,
    private val mList: ArrayList<VideoModel>,
    private val callbackInterface: CareerVideosAdapterCallbackInterface,
) :
    RecyclerView.Adapter<CareerVideosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_career_videos,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        Glide.with(context).load(mList[position].pathUrl).placeholder(R.drawable.icon_no_image)
            .into(holder.image)



        holder.itemView.setOnClickListener {
            callbackInterface.videoClickCallback(mList[position].pathUrl, position)

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val image: ImageView = itemView.findViewById(R.id.image)

    }

    interface CareerVideosAdapterCallbackInterface {
        fun videoClickCallback(uri: String, position: Int)
    }
}