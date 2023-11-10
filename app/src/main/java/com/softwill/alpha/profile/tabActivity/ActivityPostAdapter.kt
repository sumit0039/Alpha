package com.softwill.alpha.profile.tabActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R


class ActivityPostAdapter(
    private val context: ActivityFragment,
    private val callbackInterface: CallbackInterface,
    private val mList: ArrayList<PostModel>,
) :
    RecyclerView.Adapter<ActivityPostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_activity_post,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        Glide.with(context).load(mList[position].photos[0].pathUrl)
            .placeholder(R.drawable.icon_no_image)
            .into(holder.image)

        holder.image.setOnClickListener {
            callbackInterface.onImageCallback(
                position,
                mList[position].id
            )
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val image: ImageView = itemView.findViewById(R.id.image)

    }

    interface CallbackInterface {
        fun onImageCallback(
            position: Int,
            id: Int
        )
    }

}